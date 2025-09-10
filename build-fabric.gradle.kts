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
base.archivesName = mod.id

val javaVersion: JavaVersion =
    if (stonecutter.eval(stonecutter.current.version, ">=1.20.6"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17

stonecutter {
    val config = mod.getStonecutterConfiguration(stonecutter::eval)

    config.constants.forEach { entry -> constants[entry.key] = entry.value }
    config.swaps.forEach { entry -> swaps[entry.key] = entry.value }
    config.replacements.forEach { entry -> replacements.string {
        direction = entry.value
        replace(entry.key.first, entry.key.second)
    } }
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

repositories {
    maven("https://mvn.devos.one/releases") // Porting Lib releases
    maven("https://mvn.devos.one/snapshots") // Create and several dependencies
    maven("https://modmaven.dev/") // Flywheel
    maven("https://maven.jamieswhiteshirt.com/libs-release") // Reach Entity Attributes
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven") // Forge Config API Port

    maven("https://jitpack.io/") // Fabric ASM for Porting Lib
        .content { includeGroupAndSubgroups("com.github") }

    maven("https://maven.shedaniel.me") // Cloth Config, REI
    maven("https://maven.blamejared.com") // JEI

    maven("https://maven.terraformersmc.com/releases") // Mod Menu, EMI
}

configurations.configureEach {
    resolutionStrategy {
        // make sure the desired version of loader is used. Sometimes old versions are pulled in transitively.
        force("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
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
        modImplementation("com.simibubi.create:create-fabric-${minecraft}:${it}")
    }
}

java {
    targetCompatibility = javaVersion
    sourceCompatibility = javaVersion
}

tasks {
    processResources {
        fun inputProps(props: Map<String, Any>): Map<String, Any> {
            inputs.properties(*props.map { entry -> entry.key to entry.value }.toTypedArray() )
            return props
        }

        val props = inputProps(mod.getProps())
        filesMatching("fabric.mod.json") { expand(props) }

        val mixinProps = inputProps(mapOf(
            "compatibility_level" to "JAVA_${javaVersion.majorVersion}",
            "appendable_refmap" to ""
        ))
        filesMatching("*.mixins.json") { expand(mixinProps) }
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