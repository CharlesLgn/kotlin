// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: compiler/ir/serialization.common/src/KotlinIr.proto

package org.jetbrains.kotlin.backend.common.serialization.proto;

/**
 * Protobuf type {@code org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier}
 */
public final class PirBodyCarrier extends
    org.jetbrains.kotlin.protobuf.GeneratedMessageLite implements
    // @@protoc_insertion_point(message_implements:org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier)
    PirBodyCarrierOrBuilder {
  // Use PirBodyCarrier.newBuilder() to construct.
  private PirBodyCarrier(org.jetbrains.kotlin.protobuf.GeneratedMessageLite.Builder builder) {
    super(builder);
    this.unknownFields = builder.getUnknownFields();
  }
  private PirBodyCarrier(boolean noInit) { this.unknownFields = org.jetbrains.kotlin.protobuf.ByteString.EMPTY;}

  private static final PirBodyCarrier defaultInstance;
  public static PirBodyCarrier getDefaultInstance() {
    return defaultInstance;
  }

  public PirBodyCarrier getDefaultInstanceForType() {
    return defaultInstance;
  }

  private final org.jetbrains.kotlin.protobuf.ByteString unknownFields;
  private PirBodyCarrier(
      org.jetbrains.kotlin.protobuf.CodedInputStream input,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    initFields();
    int mutable_bitField0_ = 0;
    org.jetbrains.kotlin.protobuf.ByteString.Output unknownFieldsOutput =
        org.jetbrains.kotlin.protobuf.ByteString.newOutput();
    org.jetbrains.kotlin.protobuf.CodedOutputStream unknownFieldsCodedOutput =
        org.jetbrains.kotlin.protobuf.CodedOutputStream.newInstance(
            unknownFieldsOutput, 1);
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!parseUnknownField(input, unknownFieldsCodedOutput,
                                   extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
          case 8: {
            bitField0_ |= 0x00000001;
            lastModified_ = input.readInt32();
            break;
          }
          case 16: {
            bitField0_ |= 0x00000002;
            containerFieldSymbol_ = input.readInt64();
            break;
          }
        }
      }
    } catch (org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException(
          e.getMessage()).setUnfinishedMessage(this);
    } finally {
      try {
        unknownFieldsCodedOutput.flush();
      } catch (java.io.IOException e) {
      // Should not happen
      } finally {
        unknownFields = unknownFieldsOutput.toByteString();
      }
      makeExtensionsImmutable();
    }
  }
  public static org.jetbrains.kotlin.protobuf.Parser<PirBodyCarrier> PARSER =
      new org.jetbrains.kotlin.protobuf.AbstractParser<PirBodyCarrier>() {
    public PirBodyCarrier parsePartialFrom(
        org.jetbrains.kotlin.protobuf.CodedInputStream input,
        org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
        throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
      return new PirBodyCarrier(input, extensionRegistry);
    }
  };

  @java.lang.Override
  public org.jetbrains.kotlin.protobuf.Parser<PirBodyCarrier> getParserForType() {
    return PARSER;
  }

  private int bitField0_;
  public static final int LAST_MODIFIED_FIELD_NUMBER = 1;
  private int lastModified_;
  /**
   * <code>required int32 last_modified = 1;</code>
   */
  public boolean hasLastModified() {
    return ((bitField0_ & 0x00000001) == 0x00000001);
  }
  /**
   * <code>required int32 last_modified = 1;</code>
   */
  public int getLastModified() {
    return lastModified_;
  }

  public static final int CONTAINER_FIELD_SYMBOL_FIELD_NUMBER = 2;
  private long containerFieldSymbol_;
  /**
   * <code>optional int64 container_field_symbol = 2;</code>
   */
  public boolean hasContainerFieldSymbol() {
    return ((bitField0_ & 0x00000002) == 0x00000002);
  }
  /**
   * <code>optional int64 container_field_symbol = 2;</code>
   */
  public long getContainerFieldSymbol() {
    return containerFieldSymbol_;
  }

  private void initFields() {
    lastModified_ = 0;
    containerFieldSymbol_ = 0L;
  }
  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    if (!hasLastModified()) {
      memoizedIsInitialized = 0;
      return false;
    }
    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(org.jetbrains.kotlin.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    getSerializedSize();
    if (((bitField0_ & 0x00000001) == 0x00000001)) {
      output.writeInt32(1, lastModified_);
    }
    if (((bitField0_ & 0x00000002) == 0x00000002)) {
      output.writeInt64(2, containerFieldSymbol_);
    }
    output.writeRawBytes(unknownFields);
  }

  private int memoizedSerializedSize = -1;
  public int getSerializedSize() {
    int size = memoizedSerializedSize;
    if (size != -1) return size;

    size = 0;
    if (((bitField0_ & 0x00000001) == 0x00000001)) {
      size += org.jetbrains.kotlin.protobuf.CodedOutputStream
        .computeInt32Size(1, lastModified_);
    }
    if (((bitField0_ & 0x00000002) == 0x00000002)) {
      size += org.jetbrains.kotlin.protobuf.CodedOutputStream
        .computeInt64Size(2, containerFieldSymbol_);
    }
    size += unknownFields.size();
    memoizedSerializedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  protected java.lang.Object writeReplace()
      throws java.io.ObjectStreamException {
    return super.writeReplace();
  }

  public static org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier parseFrom(
      org.jetbrains.kotlin.protobuf.ByteString data)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier parseFrom(
      org.jetbrains.kotlin.protobuf.ByteString data,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier parseFrom(byte[] data)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier parseFrom(
      byte[] data,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier parseFrom(
      java.io.InputStream input,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier parseDelimitedFrom(
      java.io.InputStream input,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input, extensionRegistry);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier parseFrom(
      org.jetbrains.kotlin.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier parseFrom(
      org.jetbrains.kotlin.protobuf.CodedInputStream input,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }

  public static Builder newBuilder() { return Builder.create(); }
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder(org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier prototype) {
    return newBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() { return newBuilder(this); }

  /**
   * Protobuf type {@code org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier}
   */
  public static final class Builder extends
      org.jetbrains.kotlin.protobuf.GeneratedMessageLite.Builder<
        org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier, Builder>
      implements
      // @@protoc_insertion_point(builder_implements:org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier)
      org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrierOrBuilder {
    // Construct using org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private void maybeForceBuilderInitialization() {
    }
    private static Builder create() {
      return new Builder();
    }

    public Builder clear() {
      super.clear();
      lastModified_ = 0;
      bitField0_ = (bitField0_ & ~0x00000001);
      containerFieldSymbol_ = 0L;
      bitField0_ = (bitField0_ & ~0x00000002);
      return this;
    }

    public Builder clone() {
      return create().mergeFrom(buildPartial());
    }

    public org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier getDefaultInstanceForType() {
      return org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier.getDefaultInstance();
    }

    public org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier build() {
      org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier buildPartial() {
      org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier result = new org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
        to_bitField0_ |= 0x00000001;
      }
      result.lastModified_ = lastModified_;
      if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
        to_bitField0_ |= 0x00000002;
      }
      result.containerFieldSymbol_ = containerFieldSymbol_;
      result.bitField0_ = to_bitField0_;
      return result;
    }

    public Builder mergeFrom(org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier other) {
      if (other == org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier.getDefaultInstance()) return this;
      if (other.hasLastModified()) {
        setLastModified(other.getLastModified());
      }
      if (other.hasContainerFieldSymbol()) {
        setContainerFieldSymbol(other.getContainerFieldSymbol());
      }
      setUnknownFields(
          getUnknownFields().concat(other.unknownFields));
      return this;
    }

    public final boolean isInitialized() {
      if (!hasLastModified()) {
        
        return false;
      }
      return true;
    }

    public Builder mergeFrom(
        org.jetbrains.kotlin.protobuf.CodedInputStream input,
        org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier) e.getUnfinishedMessage();
        throw e;
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private int lastModified_ ;
    /**
     * <code>required int32 last_modified = 1;</code>
     */
    public boolean hasLastModified() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required int32 last_modified = 1;</code>
     */
    public int getLastModified() {
      return lastModified_;
    }
    /**
     * <code>required int32 last_modified = 1;</code>
     */
    public Builder setLastModified(int value) {
      bitField0_ |= 0x00000001;
      lastModified_ = value;
      
      return this;
    }
    /**
     * <code>required int32 last_modified = 1;</code>
     */
    public Builder clearLastModified() {
      bitField0_ = (bitField0_ & ~0x00000001);
      lastModified_ = 0;
      
      return this;
    }

    private long containerFieldSymbol_ ;
    /**
     * <code>optional int64 container_field_symbol = 2;</code>
     */
    public boolean hasContainerFieldSymbol() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional int64 container_field_symbol = 2;</code>
     */
    public long getContainerFieldSymbol() {
      return containerFieldSymbol_;
    }
    /**
     * <code>optional int64 container_field_symbol = 2;</code>
     */
    public Builder setContainerFieldSymbol(long value) {
      bitField0_ |= 0x00000002;
      containerFieldSymbol_ = value;
      
      return this;
    }
    /**
     * <code>optional int64 container_field_symbol = 2;</code>
     */
    public Builder clearContainerFieldSymbol() {
      bitField0_ = (bitField0_ & ~0x00000002);
      containerFieldSymbol_ = 0L;
      
      return this;
    }

    // @@protoc_insertion_point(builder_scope:org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier)
  }

  static {
    defaultInstance = new PirBodyCarrier(true);
    defaultInstance.initFields();
  }

  // @@protoc_insertion_point(class_scope:org.jetbrains.kotlin.backend.common.serialization.proto.PirBodyCarrier)
}