plugins {
    id("java-library")
    id("idea")
    id("fabric-loom") version "1.11-SNAPSHOT"
    kotlin("jvm") version "2.2.10"
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
    id("dev.kikugie.stonecutter")
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.17"
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

class ModData {
    val version = property("mod.version") as String
    val group = property("mod.group") as String
    val id = property("mod.id") as String
    val name = property("mod.name") as String
    val authors = property("mod.authors") as String
    val description = property("mod.description") as String
    val homepage = property("mod.homepage") as String
    val sources = property("mod.sources") as String
    val issues = property("mod.issues") as String
    val license = property("mod.license") as String
}

val mod = ModData()
val minecraft = property("deps.minecraft") as String
val moonlight = property("deps.moonlight") as String

val is120 = stonecutter.eval(stonecutter.current.version, "<1.21")

val platform = property("modstitch.platform") as String

val isFabric = platform == "loom"
val isNeoforge = platform == "moddevgradle"
val isForge = platform == "moddevgradle-legacy"
val isForgeLike = isNeoforge || isForge
val loaderName = when {
    isFabric -> "Fabric"
    isNeoforge -> "NeoForge"
    isForge -> "Forge"
    else -> error("Unknown loader")
}
val loader = loaderName.lowercase()
stonecutter {
    constants.match(
        loader,
        "fabric",
        "neoforge",
        "forge",
    )
    constants["forgeLike"] = isForgeLike

    swaps["client_only"] = when {
        isForge -> "@net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)"
        isFabric -> "@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)"
        else -> "@net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)"
    }

    swaps["public_now_protected"] = when {
        is120 -> "public"
        else -> "protected"
    }

    swaps ["recipe_result"] = when {
        is120 -> "\"item\":"
        else -> "\"id\":"
    }
}


loom {
    runConfigs.all {
        ideConfigGenerated(false)
    }

    runs {
        register("testClient") {
            client()
            name = "Test Client"
            vmArgs("-Dmixin.debug.export=true")
            ideConfigGenerated(true)
        }
        register("testServer") {
            server()
            name = "Test Server"
            ideConfigGenerated(true)
        }
    }
}

fletchingTable {
    mixins.create("main") {
        automatic = false

        mixin("default", "condiments.mixins.json")
        if (isFabric) mixin("fabric", "condiments-fabric.mixins.json")
        if (is120) mixin("120", "condiments-120.mixins.json")
    }

    j52j.register("main") {
        extension("json",
            "data/condiments/**/*.json5"
        )

        if (is120) extension("json",
            "data/condiments/item_modifier/* -> ../item_modifiers",
            "data/condiments/loot_table/blocks/* -> ../../loot_tables/blocks",

            "data/**/tags/block/* -> ../blocks",
            "data/**/tags/block/mineable/* -> ../../blocks/mineable",
            "data/**/tags/item/* -> ../items",

            "data/condiments/recipe/* -> ../recipes",
            "data/condiments/recipe/accents/* -> ../../recipes/accents",
            "data/condiments/recipe/blackened_iron/* -> ../../recipes/blackened_iron",
            "data/condiments/recipe/polished_wood/* -> ../../recipes/polished_wood",
            "data/condiments/recipe/wood_walls/* -> ../../recipes/wood_walls",

            "data/condiments/advancement/recipes/* -> ../../advancements/recipes",
            "data/condiments/advancement/recipes/accents/* -> ../../../advancements/recipes/accents",
            "data/condiments/advancement/recipes/blackened_iron/* -> ../../../advancements/recipes/blackened_iron",
            "data/condiments/advancement/recipes/polished_wood/* -> ../../../advancements/recipes/polished_wood",
            "data/condiments/advancement/recipes/wood_walls/* -> ../../../advancements/recipes/wood_walls",
        )
    }
}

// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    minecraft("com.mojang:minecraft:${minecraft}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")

    modImplementation("maven.modrinth:moonlight:${moonlight}-${loader}")

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
    prop("deps.ponder") { modCompileOnly("net.createmod.ponder:Ponder-${loaderName}-${minecraft}:${it}") }
    prop("deps.flywheel") { modCompileOnly("dev.engine-room.flywheel:flywheel-${loader}-api-${minecraft}:${it}") }
    prop("deps.registrate") { modCompileOnly("com.tterrag.registrate:Registrate:${it}") }
}

tasks {
    processResources {
        fun <V: Any> propEntry(key: String, value: V): Pair<String, V> {
            inputs.property(key, value)
            return key to value
        }

        val props = mapOf(
            propEntry("mod_id", mod.id),
            propEntry("mod_name", mod.name),
            propEntry("mod_version", "${mod.version}+$minecraft-$loader"),
            propEntry("mod_group", mod.group),
            propEntry("mod_author", mod.authors),
            propEntry("mod_license", mod.license),
            propEntry("mod_description", mod.description),
            propEntry("mod_homepage", mod.homepage),
            propEntry("mod_sources", mod.sources),
            propEntry("mod_issues", mod.issues),
            propEntry("mod_author_list", mod.authors.split(", ").joinToString("\",\"")),
            propEntry("minecraft", minecraft),
            propEntry("moonlight", moonlight),
        )

        filesMatching("fabric.mod.json") { expand(props) }
    }

    register<Copy>("buildAndCollect") {
        group = "build"

        into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
        dependsOn("build")
    }

    named("jar") {
        dependsOn("cleanupArtifacts")
    }

    register<Delete>("cleanupArtifacts") {
        delete(layout.buildDirectory.dir("resources/main/META-INF"))
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}