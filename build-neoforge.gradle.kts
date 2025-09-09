plugins {
    id("java-library")
    id("idea")
    id("net.neoforged.moddev") version "2.0.107"
    kotlin("jvm") version "2.2.10"
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
    id("dev.kikugie.stonecutter")
    id("dev.kikugie.fletching-table") version "0.1.0-alpha.17"
    id("mod-build-common")
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

val mod = `mod-common`.mod.get()
val deps = mod.deps
val minecraft = deps.minecraft
val loader = deps.loader.id()

stonecutter {
    val config = mod.getStonecutterConfiguration(stonecutter::eval)

    config.constants.forEach { entry -> constants[entry.key] = entry.value }
    config.swaps.forEach { entry -> swaps[entry.key] = entry.value }
}

neoForge {
    prop("deps.neoforge") { version = it }

    runs {
        register("testClient") {
            client()
        }
        register("testServer") {
            server()
        }
    }

    mods {
        create(mod.id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

fletchingTable {
    val config = mod.getFletchingTableConfiguration(stonecutter::eval)

    mixins.create("main") {
        automatic = false

        mixin("default", "condiments.mixins.json")
        if (stonecutter.eval(stonecutter.current.version, "<1.21"))
            mixin("120", "condiments-120.mixins.json")
    }

    j52j.register("main") {
        extension("json",
            "data/condiments/**/*.json5"
        )

        config.extensions.forEach { entry -> extension(entry.first, *entry.second) }
    }
}

// Sets up a dependency configuration called 'localRuntime'.
// This configuration should be used instead of 'runtimeOnly' to declare
// a dependency that will be present for runtime testing but that is
// "optional", meaning it will not be pulled by dependents of this mod.
configurations {
    create("localRuntime")

    runtimeClasspath.get().apply {
        extendsFrom(configurations["localRuntime"])
    }
}

// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    val localRuntime = configurations["localRuntime"]

    implementation("maven.modrinth:moonlight:${deps.moonlight}-${loader}")

    val recipeViewer = property("test.recipe_viewer") as String
    prop("deps.jei") {
        compileOnly("mezz.jei:jei-${minecraft}-${loader}-api:${it}")
        if (recipeViewer.equals("jei", true))
            localRuntime("mezz.jei:jei-${minecraft}-${loader}:${it}")
    }
    prop("deps.rei") {
        compileOnly("me.shedaniel:RoughlyEnoughItems-api-${loader}:${it}")
        compileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-${loader}:${it}")
        if (recipeViewer.equals("rei", true))
            localRuntime("me.shedaniel:RoughlyEnoughItems-${loader}:${it}")
    }
    prop("deps.emi") {
        compileOnly("dev.emi:emi-${loader}:${it}+${minecraft}:api")
        if (recipeViewer.equals("emi", true))
            localRuntime("dev.emi:emi-${loader}:${it}+${minecraft}")
    }

    prop("deps.create") {
        compileOnly("com.simibubi.create:create-${minecraft}:${it}:slim") { isTransitive = false }
    }
    prop("deps.ponder") { compileOnly("net.createmod.ponder:Ponder-${deps.loader.formattedName}-${minecraft}:${it}") }
    prop("deps.flywheel") { compileOnly("dev.engine-room.flywheel:flywheel-${loader}-api-${minecraft}:${it}") }
    prop("deps.registrate") { compileOnly("com.tterrag.registrate:Registrate:${it}") }
}

tasks {
    processResources {
        val props = mod.getProps()
        inputs.properties(*props.map { entry -> entry.key to entry.value }.toTypedArray() )

        filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    named("jar") {
        dependsOn("cleanupArtifacts")
    }

    register<Delete>("cleanupArtifacts") {
        delete(layout.buildDirectory.file("resources/main/fabric.mod.json"))
        delete(layout.buildDirectory.file("resources/main/META-INF/mods.toml"))
    }

    register<Copy>("buildAndCollect") {
        group = "build"

        into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
        dependsOn("build")
    }

    register<Delete>("buildCollectAndClean") {
        group = "build"

        delete(layout.buildDirectory.dir("libs"))
        delete(layout.buildDirectory.dir("devlibs"))

        dependsOn("buildAndCollect")
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}