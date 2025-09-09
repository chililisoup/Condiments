pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        // Loom platform
        maven("https://maven.fabricmc.net/")

        // MDG platform
        maven("https://maven.neoforged.net/releases/")

        // Stonecutter, Fletching Table
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    kotlin("jvm") version "2.2.10" apply false
    id("com.google.devtools.ksp") version "2.2.10-2.0.2" apply false
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
    id("dev.kikugie.stonecutter") version "0.7.10"
}

stonecutter {
    create(rootProject) {
        fun mc(mcVersion: String, name: String = mcVersion, loaders: Iterable<String>) =
            loaders.forEach { version("$name-$it", mcVersion).buildscript("build-$it.gradle.kts") }

        mc("1.21.1", loaders = listOf("fabric", "neoforge"))
        mc("1.20.1", loaders = listOf("fabric", "forge"))

        vcsVersion = "1.21.1-fabric"
    }
}

rootProject.name = "Condiments"
