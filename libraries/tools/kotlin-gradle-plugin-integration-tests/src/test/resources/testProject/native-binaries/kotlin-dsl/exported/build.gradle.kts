plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    sourceSets["commonMain"].apply {
        dependencies {
            api("org.jetbrains.kotlin:kotlin-stdlib-common")
        }
    }

    iosArm64("ios") 
    macosX64("macos64")
    linuxX64("linux64")
    mingwX64("mingw64")
    iosX64("iosSim")
}
