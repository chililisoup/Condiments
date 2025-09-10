plugins {
    id("fabric-loom") version "1.11-SNAPSHOT" apply false
    val moddevVersion = "2.0.107"
    id("net.neoforged.moddev") version moddevVersion apply false
    id("net.neoforged.moddev.legacyforge") version moddevVersion apply false
    id("dev.kikugie.stonecutter")
    val fletchingTableVersion = "0.1.0-alpha.17"
    id("dev.kikugie.fletching-table.fabric") version fletchingTableVersion apply false
    id("dev.kikugie.fletching-table.neoforge") version fletchingTableVersion apply false
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

        maven("https://server.bbkr.space/artifactory/libs-release/")
        maven("https://maven.quiltmc.org/repository/release")
    }
}
