plugins {
    id("java-library")
    id("idea")
    id("net.neoforged.moddev.legacyforge")
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("dev.kikugie.stonecutter")
    id("dev.kikugie.fletching-table")
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

legacyForge {
    prop("deps.forge") { version = "$minecraft-$it" }

    runs {
        register("client") {
            client()
        }
        register("server") {
            server()
        }
    }

    mods {
        create(mod.id) {
            sourceSet(sourceSets.main.get())
        }
    }

    parchment {
        minecraftVersion = minecraft
        prop("deps.parchment") { mappingsVersion = it }
    }
}

mixin {
    add(sourceSets.main.get(), "${mod.id}.refmap.json")
    config("${mod.id}.mixins.json")
}

fletchingTable {
    val config = mod.getFletchingTableConfiguration(stonecutter::eval)

    mixins.create("main") {
        mixin("default", "condiments.mixins.json") {
            env("MAIN")
            env("CLIENT", "dev.chililisoup.condiments.mixin.client")
        }
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

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    prop("deps.mixinextras") {
        compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:${it}")!!)
        implementation(jarJar("io.github.llamalad7:mixinextras-forge:${it}")) {}
    }
    compileOnly("org.jetbrains:annotations:20.1.0")

    modImplementation("maven.modrinth:moonlight:${deps.moonlight}-${loader}")

    val recipeViewer = property("test.recipe_viewer") as String
    prop("deps.jei") {
        modCompileOnly("mezz.jei:jei-${minecraft}-common-api:${it}")
        modCompileOnly("mezz.jei:jei-${minecraft}-${loader}-api:${it}")
        if (recipeViewer.equals("jei", true))
            localRuntime(modRuntimeOnly("mezz.jei:jei-${minecraft}-${loader}:${it}") {})
    }
    prop("deps.rei") {
        modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-${loader}:${it}")
        modCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-${loader}:${it}")
        if (recipeViewer.equals("rei", true))
            localRuntime(modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-${loader}:${it}") {})
    }
    prop("deps.emi") {
        modCompileOnly("dev.emi:emi-${loader}:${it}+${minecraft}:api")
        if (recipeViewer.equals("emi", true))
            localRuntime(modRuntimeOnly("dev.emi:emi-${loader}:${it}+${minecraft}") {})
    }

    prop("deps.create") {
        modCompileOnly("com.simibubi.create:create-${minecraft}:${it}:slim") { isTransitive = false }
    }
    prop("deps.ponder") { modCompileOnly("net.createmod.ponder:Ponder-${deps.loader.formattedName}-${minecraft}:${it}") }
    prop("deps.flywheel") { modCompileOnly("dev.engine-room.flywheel:flywheel-${loader}-api-${minecraft}:${it}") }
    prop("deps.registrate") { modCompileOnly("com.tterrag.registrate:Registrate:${it}") }
}

java {
    withSourcesJar()
    val requiresJava21: Boolean = stonecutter.eval(stonecutter.current.version, ">=1.20.6")
    val javaVersion: JavaVersion =
        if (requiresJava21) JavaVersion.VERSION_21
        else JavaVersion.VERSION_17
    targetCompatibility = javaVersion
    sourceCompatibility = javaVersion
}

tasks {
    processResources {
        val props = mod.getProps()
        inputs.properties(*props.map { entry -> entry.key to entry.value }.toTypedArray() )

        filesMatching("META-INF/mods.toml") { expand(props) }
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    named("jar") {
        dependsOn("cleanupArtifacts")
    }

    register<Delete>("cleanupArtifacts") {
        delete(layout.buildDirectory.file("resources/main/fabric.mod.json"))
        delete(layout.buildDirectory.file("resources/main/META-INF/neoforge.mods.toml"))
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