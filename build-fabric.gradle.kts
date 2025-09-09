plugins {
    id("java-library")
    id("idea")
    id("fabric-loom")
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("dev.kikugie.stonecutter")
    id("dev.kikugie.fletching-table.fabric")
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

version = mod.archiveVersion
base.archivesName = mod.name

stonecutter {
    val config = mod.getStonecutterConfiguration(stonecutter::eval)

    config.constants.forEach { entry -> constants[entry.key] = entry.value }
    config.swaps.forEach { entry -> swaps[entry.key] = entry.value }
}

loom {
    runConfigs.all {
        vmArgs("-Dmixin.debug.export=true")
        ideConfigGenerated(true)
    }
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

// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    minecraft("com.mojang:minecraft:${minecraft}")
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        prop("deps.parchment") { parchment("org.parchmentmc.data:parchment-${minecraft}:${it}@zip") }
    })

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")

    modImplementation("maven.modrinth:moonlight:${deps.moonlight}-${loader}")

    prop("deps.mod_menu") { modImplementation("com.terraformersmc:modmenu:${it}") }

    val recipeViewer = property("test.recipe_viewer") as String
    prop("deps.jei") {
        modCompileOnly("mezz.jei:jei-${minecraft}-${loader}-api:${it}")
        if (recipeViewer.equals("jei", true))
            modLocalRuntime("mezz.jei:jei-${minecraft}-${loader}:${it}")
    }
    prop("deps.rei") {
        modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-${loader}:${it}")
        modCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-${loader}:${it}")
        if (recipeViewer.equals("rei", true))
            modLocalRuntime("me.shedaniel:RoughlyEnoughItems-${loader}:${it}")
    }
    prop("deps.emi") {
        modCompileOnly("dev.emi:emi-${loader}:${it}+${minecraft}:api")
        if (recipeViewer.equals("emi", true))
            modLocalRuntime("dev.emi:emi-${loader}:${it}+${minecraft}")
    }

    prop("deps.create") {
        modCompileOnly("com.simibubi.create:create-${minecraft}:${it}:slim") { isTransitive = false }
    }
    prop("deps.ponder") { modCompileOnly("net.createmod.ponder:Ponder-${deps.loader.formattedName}-${minecraft}:${it}") }
    prop("deps.flywheel") { modCompileOnly("dev.engine-room.flywheel:flywheel-${loader}-api-${minecraft}:${it}") }
    prop("deps.registrate") { modCompileOnly("com.tterrag.registrate:Registrate:${it}") }
}

java {
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

        filesMatching("fabric.mod.json") { expand(props) }
    }

    named("classes") {
        dependsOn("filterResources")
    }

    register<Delete>("filterResources") {
        delete(layout.buildDirectory.dir("resources/main/META-INF"))
        delete(layout.buildDirectory.dir("resources/main/data/neoforge"))

        dependsOn("processResources")
    }

    register<Copy>("buildAndCollect") {
        group = "build"

        from(layout.buildDirectory.dir("libs"))
        include("*.jar")
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