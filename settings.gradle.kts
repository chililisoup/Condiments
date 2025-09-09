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
    id("dev.kikugie.stonecutter") version "0.7.10"
}

stonecutter {
    create(rootProject, file("versions.json5")) {
        vcsVersion = "1.21.1-fabric"
    }
}

rootProject.name = "Condiments"
