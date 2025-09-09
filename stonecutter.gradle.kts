plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.1-fabric"

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")

        exclusiveContent {
            forRepository {
                maven("https://api.modrinth.com/maven") { name = "Modrinth "}
            }
            filter {
                includeGroup("maven.modrinth")
            }
        }

        maven("https://maven.terraformersmc.com/") // MixinExtras, Mod Menu, EMI
        maven("https://maven.blamejared.com/") // JEI
        maven("https://maven.shedaniel.me/") // REI
        maven("https://maven.createmod.net") // Create, Ponder, Flywheel
        maven("https://maven.ithundxr.dev/snapshots") // Registrate
        maven("https://maven.ithundxr.dev/mirror") // Registrate (1.20.1)
    }
}

subprojects {
    tasks {
        register<Delete>("buildCollectAndClean") {
            group = "build"

            delete(layout.buildDirectory.dir("libs"))
            delete(layout.buildDirectory.dir("devlibs"))

            dependsOn("buildAndCollect")
        }

        register<Delete>("deleteBuildCache") {
            group = "build"

            delete(layout.buildDirectory)
        }
    }
}