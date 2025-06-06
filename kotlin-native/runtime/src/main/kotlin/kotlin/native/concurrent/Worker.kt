/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */
@file:OptIn(ExperimentalForeignApi::class)
package kotlin.native.concurrent

import kotlin.native.internal.IntrinsicType
import kotlin.native.internal.TypedIntrinsic
import kotlin.native.internal.ref.*
import kotlinx.cinterop.*

/**
 * Class representing a worker.
 *
 * [Worker] represents a thread of execution _and_ a queue of work sent to this worker.
 * Once started, the worker processes its own queue until it is explicitly [terminated][requestTermination].
 *
 * Worker's API resembles both executor and threads API:
 *
 * - It is a resource that has to be [closed][requestTermination].
 *     - Failure to terminatae **and** join the termination request leads to native memory leak.
 *     - It cannot be terminated externally while processing the work queue, leading to a crash.
 * - It can be [parked][park], but while parked, worker is allowed to process its own tasks' queue.
 *     - In order to unpark a worker, any task has to be submited to the worker.
 * - The work can be sent to the worker both in forms of regular [runnables][execute] and [delayed (timed)][executeAfter] runnables.
 *     - There is no mechanism to cancel delayed runnables.
 * - Attempts to invoke any method of worker being terminated lead to a runtime exception.
 *     - It includes such things as innocuous [Worker.current] from the **current** worker.
 */
@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
@ObsoleteWorkersApi
public value class Worker @PublishedApi internal constructor(public val id: Int) {
    public companion object {
        /**
         * Start new scheduling primitive, such as thread, to accept new tasks via `execute` interface.
         * Typically new worker may be needed for computations offload to another core, for IO it may be
         * better to use non-blocking IO combined with more lightweight coroutines.
         *
         * @param errorReporting controls if an uncaught exceptions in the worker will be reported.
         * @param name defines the optional name of this worker, if none - default naming is used.
         * @return worker object, usable across multiple concurrent contexts.
         */
        public fun start(errorReporting: Boolean = true, name: String? = null): Worker
                = Worker(startInternal(errorReporting, createRetainedExternalRCRef(name)))

        /**
         * Return the current worker. Worker context is accessible to any valid Kotlin context,
         * but only actual active worker produced with [Worker.start] automatically processes execution requests.
         * For other situations [processQueue] must be called explicitly to process request queue.
         * @return current worker object, usable across multiple concurrent contexts.
         */
        public val current: Worker get() = Worker(currentInternal())

        /**
         * Create worker object from a C pointer.
         *
         * This function is deprecated. See [Worker.asCPointer] for more details.
         *
         * @param pointer value returned earlier by [Worker.asCPointer]
         */
        @Deprecated("Use kotlinx.cinterop.StableRef instead", level = DeprecationLevel.WARNING)
        public fun fromCPointer(pointer: COpaquePointer?): Worker =
                if (pointer != null) Worker(pointer.toLong().toInt()) else throw IllegalArgumentException()

        /**
         * Get a list of all unterminated workers.
         *
         * Thread safety: If some other thread calls [Worker.requestTermination] at the same time then this may
         * return a [Worker] that's already terminated.
         */
        @ExperimentalStdlibApi
        public val activeWorkers: List<Worker>
            get() = getActiveWorkersInternal().map { Worker(it) }
    }

    /**
     * Requests termination of the worker.
     *
     * Returns [Future] that **must** be joined with [Future.result] blocking call.
     * Failure to do so will leak native memory and underlying native thread handles.
     *
     * @param processScheduledJobs controls is we shall wait until all scheduled jobs processed,
     * or terminate immediately. If there are jobs to be execucted with [executeAfter] their execution
     * is awaited for.
     */
    public fun requestTermination(processScheduledJobs: Boolean = true): Future<Unit> =
            Future<Unit>(requestTerminationInternal(id, processScheduledJobs))

    /**
     * Plan job for further execution in the worker. Execute is a two-phase operation:
     * 1. [producer] function is executed on the caller's thread.
     * 2. the result of [producer] and [job] function pointer is being added to jobs queue
     * of the selected worker. Note that [job] must not capture any state itself.
     *
     * Parameter [mode] has no effect.
     *
     * Behavior is more complex in case of legacy memory manager:
     *
     * - first [producer] function is executed, and resulting object and whatever it refers to
     * is analyzed for being an isolated object subgraph, if in checked mode.
     * - Afterwards, this disconnected object graph and [job] function pointer is being added to jobs queue
     * of the selected worker. Note that [job] must not capture any state itself, so that whole state is
     * explicitly stored in object produced by [producer]. Scheduled job is being executed by the worker,
     * and result of such a execution is being disconnected from worker's object graph. Whoever will consume
     * the future, can use result of worker's computations.
     * Note, that some technically disjoint subgraphs may lead to `kotlin.IllegalStateException`
     * so `kotlin.native.runtime.GC.collect()` could be called in the end of `producer` and `job`
     * if garbage cyclic structures or other uncollected objects refer to the value being transferred.
     *
     * @return the future with the computation result of [job].
     */
    @Suppress("UNUSED_PARAMETER")
    @TypedIntrinsic(IntrinsicType.WORKER_EXECUTE)
    public fun <T1, T2> execute(mode: TransferMode, producer: () -> T1, job: (T1) -> T2): Future<T2> =
            /*
             * This function is a magical operation, handled by lowering in the compiler, and replaced with call to
             *   executeImpl(worker, mode, producer, job)
             * but first ensuring that `job` parameter  doesn't capture any state.
             */
            throw RuntimeException("Shall not be called directly")

    /**
     * Plan job for further execution in the worker.
     *
     * If the worker was created with `errorReporting` set to true, any exception escaping from [operation] will
     * be handled by [processUnhandledException].
     *
     * @param afterMicroseconds defines after how many microseconds delay execution shall happen, 0 means immediately,
     * @throws [IllegalArgumentException] on negative values of [afterMicroseconds].
     * @throws [IllegalStateException] if [operation] parameter is not frozen and worker is not current.
     */
    public fun executeAfter(afterMicroseconds: Long = 0, operation: () -> Unit): Unit {
        if (afterMicroseconds < 0) throw IllegalArgumentException("Timeout parameter must be non-negative")
        executeAfterInternal(id, createRetainedExternalRCRef(operation), afterMicroseconds)
    }

    /**
     * Process pending job(s) on the queue of this worker.
     * Note that jobs scheduled with [executeAfter] using non-zero timeout are
     * not processed this way. If termination request arrives while processing the queue via this API,
     * worker is marked as terminated and will exit once the current request is done with.
     *
     * @throws [IllegalStateException] if this request is executed on non-current [Worker].
     * @return `true` if request(s) was processed and `false` otherwise.
     */
    public fun processQueue(): Boolean = processQueueInternal(id)

    /**
     * Park execution of the current worker until a new request arrives or timeout specified in
     * [timeoutMicroseconds] elapsed. If [process] is true, pending queue elements are processed,
     * including delayed requests. Note that multiple requests could be processed this way.
     *
     * @param timeoutMicroseconds defines how long to park worker if no requests arrive, waits forever if -1.
     * @param process defines if arrived request(s) shall be processed.
     * @return if [process] is `true`: if request(s) was processed `true` and `false` otherwise.
     *   if [process] is `false`:` true` if request(s) has arrived and `false` if timeout happens.
     * @throws [IllegalStateException] if this request is executed on non-current [Worker].
     * @throws [IllegalArgumentException] if timeout value is incorrect.
     */
    public fun park(timeoutMicroseconds: Long, process: Boolean = false): Boolean {
        if (timeoutMicroseconds < -1) throw IllegalArgumentException()
        return parkInternal(id, timeoutMicroseconds, process)
    }

    /**
     * Name of the worker, as specified in [Worker.start] or "worker $id" by default,
     *
     * @throws [IllegalStateException] if this request is executed on an invalid worker.
     */
    public val name: String
        get() {
            val customName = dereferenceExternalRCRef(getWorkerNameInternal(id)) as String?
            return if (customName == null) "worker $id" else customName
        }

    /**
     * String representation of the worker.
     */
    override public fun toString(): String = "Worker $name"

    /**
     * Convert worker to a COpaquePointer value that could be passed via native void* pointer.
     * Can be used as an argument of [Worker.fromCPointer].
     *
     * This function is deprecated. Use `kotlinx.cinterop.StableRef.create(worker).asCPointer()` instead.
     * The result can be unwrapped with `pointer.asStableRef<Worker>().get()`.
     * [StableRef] should be eventually disposed manually with [StableRef.dispose].
     *
     * @return worker identifier as C pointer.
     */
    @Deprecated("Use kotlinx.cinterop.StableRef instead", level = DeprecationLevel.WARNING)
    public fun asCPointer() : COpaquePointer? = id.toLong().toCPointer()

    /**
     * Get platform thread id of the worker thread.
     *
     * Usually returns `pthread_t` casted to [ULong].
     */
    @ExperimentalStdlibApi
    public val platformThreadId: ULong
        get() = getPlatfromThreadIdInternal(id)
}

/**
 * Executes [block] with new [Worker] as resource, by starting the new worker, calling provided [block]
 * (in current context) with newly started worker as [this] and terminating worker after the block completes.
 * Note that this operation is pretty heavyweight, use preconfigured worker or worker pool if need to
 * execute it frequently.
 *
 * @param name of the started worker.
 * @param errorReporting controls if uncaught errors in worker to be reported.
 * @param block to be executed.
 * @return value returned by the block.
 */
@ObsoleteWorkersApi
public inline fun <R> withWorker(name: String? = null, errorReporting: Boolean = true, block: Worker.() -> R): R {
    val worker = Worker.start(errorReporting, name)
    try {
        return worker.block()
    } finally {
        worker.requestTermination().result
    }
}
