package transitiveStory.midActual.commonSource


actual open class SomeMPPInTheCommon actual constructor() {
    actual val simpleVal: Int = 16

    actual companion object Compainon {
        actual val inTheCompanionOfBottomActualDeclarations: String = "I'm the string in `$sourceSetName` source set"
    }

}

actual val sourceSetName: String = "jvm16Main"
