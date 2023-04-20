plugins {
    kotlin("multiplatform")
    id("tz.co.asoft.library")
    id("io.codearte.nexus-staging")
    signing
}

kotlin {
    if (Targeting.JVM) jvm {
        library()
        withJava()
    }

    if (Targeting.JS) js(IR) { library() }
    if (Targeting.WASM) wasm { library() }
    val osxTargets = if (Targeting.OSX) osxTargets() else listOf()
    val ndkTargets = if (Targeting.NDK) ndkTargets() else listOf()
    val linuxTargets = if (Targeting.LINUX) linuxTargets() else listOf()
    val mingwTargets = if (Targeting.MINGW) mingwTargets() else listOf()

    val nativeTargets = osxTargets + ndkTargets + linuxTargets + mingwTargets

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("test"))
            }
        }

        if (Targeting.JVM) {
            val jvmMain by getting {
                dependencies {
                    api(kotlin("test-junit5"))
                }
            }
        }

        val nonJvmMain by creating {
            dependsOn(commonMain)
        }

        val nativeMain by creating {
            dependsOn(nonJvmMain)
        }

        if (Targeting.JS) {
            val jsMain by getting {
                dependsOn(nonJvmMain)
            }
        }

        if (Targeting.WASM) {
            val wasmMain by getting {
                dependsOn(nonJvmMain)
            }
        }

        nativeTargets.forEach {
            val main by it.compilations.getting {}
            main.defaultSourceSet {
                dependsOn(nativeMain)
            }
        }
    }
}

aSoftOSSLibrary(
    version = asoft.versions.root.get(),
    description = "A Minimal kotlin multiplatform assertion library"
)