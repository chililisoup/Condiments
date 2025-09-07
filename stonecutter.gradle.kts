plugins {
    id("dev.kikugie.stonecutter")
    id("dev.isxander.modstitch.base") version "0.6.3-unstable" apply false
}
stonecutter active "1.21.1-fabric"

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.isxander.dev/releases") // Modstitch
        maven("https://maven.terraformersmc.com/") // MixinExtras, Mod Menu, EMI
        maven("https://maven.blamejared.com/") // JEI
        maven("https://maven.shedaniel.me/") // REI
        maven("https://maven.createmod.net") // Create, Ponder, Flywheel
        maven("https://maven.ithundxr.dev/snapshots") // Registrate
        maven("https://maven.ithundxr.dev/mirror") // Registrate (1.20.1)
    }
}