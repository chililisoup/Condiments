plugins {
    id("dev.kikugie.stonecutter")
    id("dev.isxander.modstitch.base")
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

val isFabric = modstitch.isLoom
val isNeoforge = modstitch.isModDevGradleRegular
val isForge = modstitch.isModDevGradleLegacy
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
    constants["forgeLike"] = modstitch.isModDevGradle

    swaps["client_only"] = when {
        isFabric -> "@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)"
        else -> "@net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)"
    }
}

modstitch {
    minecraftVersion = minecraft

    // Alternatively use stonecutter.eval if you have a lot of versions to target.
    // https://stonecutter.kikugie.dev/stonecutter/guide/setup#checking-versions
    javaVersion = when (minecraft) {
        "1.20.1" -> 17
        "1.21.1" -> 21
        else -> throw IllegalArgumentException("Please store the java version for $minecraft in build.gradle.kts!")
    }

    // If parchment doesn't exist for a version yet, you can safely
    // omit the "deps.parchment" property from your versioned gradle.properties
    parchment {
        prop("deps.parchment") { mappingsVersion = it }
    }

    // This metadata is used to fill out the information inside
    // the metadata files found in the templates folder.
    metadata {
        modId = mod.id
        modName = mod.name
        modVersion = "${mod.version}+$minecraft-$loader"
        modGroup = mod.group
        modAuthor = mod.authors
        modLicense = mod.license
        modDescription = mod.description

        fun <K: Any, V: Any> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            put("mod_homepage", mod.homepage)
            put("mod_sources", mod.sources)
            put("mod_issues", mod.issues)
            put("mod_author_list", mod.authors.split(", ").joinToString("\",\""))
            put("minecraft", minecraft)
            prop("deps.moonlight") { put("moonlight", it) }
        }
    }

    // Fabric Loom (Fabric)
    loom {
        prop("deps.fabric_loader") { fabricLoaderVersion = it }

        // Configure loom like normal in this block.
        configureLoom {
            runConfigs.all {
                ideConfigGenerated(false)
            }

            runs {
                register("testClient") {
                    client()
                    name = "Test Client"
                    vmArgs("-Dmixin.debug.export=true")
                    runDir = "../../run/fabric"
                    ideConfigGenerated(true)
                }
                register("testServer") {
                    server()
                    name = "Test Server"
                    runDir = "../../run/fabric"
                    ideConfigGenerated(true)
                }
            }
        }
    }

    // ModDevGradle (NeoForge, Forge, Forgelike)
    moddevgradle {
        prop("deps.neoforge") { neoForgeVersion = it }
        prop("deps.forge") { forgeVersion = it }

        configureNeoForge {
            runs {
                register("testClient") {
                    client()
                    gameDirectory = layout.projectDirectory.dir("../../run/neoforge")
                }
                register("testServer") {
                    server()
                    gameDirectory = layout.projectDirectory.dir("../../run/neoforge")
                }
            }
        }
    }

    mixin {
        addMixinsToModManifest = true
        configs.register(mod.id)
        if (isFabric) configs.register("${mod.id}-fabric")
    }
}

// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    fun Dependency?.jij() = this?.also(::modstitchJiJ)

    if (isFabric) {
        prop("deps.fabric_api") { modstitchModApi("net.fabricmc.fabric-api:fabric-api:${it}") }

        prop("deps.mod_menu") { modstitchModImplementation("com.terraformersmc:modmenu:${it}") }
    }

    if (isForge) {
        prop("deps.mixinextras") { compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:${it}")!!) }
        prop("deps.mixinextras") { implementation("io.github.llamalad7:mixinextras-forge:${it}").jij() }
        compileOnly("org.jetbrains:annotations:20.1.0")
    }

    // Anything else in the dependencies block will be used for all platforms.
    prop("deps.moonlight") {
        // Implementation crashes on Fabric for some reason, just download the mod and put it into the run's mod folder
        if (isFabric) modstitchModCompileOnly("maven.modrinth:moonlight:${it}-${loader}")
        else modstitchModImplementation("maven.modrinth:moonlight:${it}-${loader}")
    }

    val recipeViewer = property("test.recipe_viewer") as String
    prop("deps.jei") {
        modstitchModCompileOnly("mezz.jei:jei-${minecraft}-${loader}-api:${it}")
        if (recipeViewer.equals("jei", true))
            modstitchModLocalRuntime("mezz.jei:jei-${minecraft}-${loader}:${it}")
    }
    prop("deps.rei") {
        modstitchModCompileOnly("me.shedaniel:RoughlyEnoughItems-api-${loader}:${it}")
        modstitchModCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-${loader}:${it}")
        if (recipeViewer.equals("rei", true))
            modstitchModLocalRuntime("me.shedaniel:RoughlyEnoughItems-${loader}:${it}")
    }
    prop("deps.emi") {
        modstitchModCompileOnly("dev.emi:emi-${loader}:${it}+${minecraft}:api")
        if (recipeViewer.equals("emi", true))
            modstitchModLocalRuntime("dev.emi:emi-${loader}:${it}+${minecraft}")
    }

    prop("deps.create") {
        modstitchModCompileOnly("com.simibubi.create:create-${minecraft}:${it}:slim") { isTransitive = false }
    }
    prop("deps.ponder") { modstitchModCompileOnly("net.createmod.ponder:Ponder-${loaderName}-${minecraft}:${it}") }
    prop("deps.flywheel") { modstitchModCompileOnly("dev.engine-room.flywheel:flywheel-${loader}-api-${minecraft}:${it}") }
    prop("deps.registrate") { modstitchModCompileOnly("com.tterrag.registrate:Registrate:${it}") }
}

tasks.named("generateModMetadata") {
    dependsOn("stonecutterGenerate")
}

modstitch.onEnable {
    modstitch.moddevgradle {
        tasks.named("createMinecraftArtifacts") {
            dependsOn("stonecutterGenerate")
        }
    }

    val finalJarTasks = listOf(
        modstitch.finalJarTask
    )

    tasks.register<Copy>("buildAndCollect") {
        group = "build"

        finalJarTasks.forEach { jar ->
            dependsOn(jar)
            from(jar.flatMap { it.archiveFile })
        }

        into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
        dependsOn("build")
    }
}

tasks.named("jar") {
    dependsOn("filterArtifacts")
}
tasks.register<Delete>("filterArtifacts") {
    when (loader) {
        "fabric" -> delete(layout.buildDirectory.dir("resources/main/META-INF"))
        "neoforge" -> delete(layout.buildDirectory.file("resources/main/META-INF/neoforge.mods.toml"))
        "forge" -> delete(layout.buildDirectory.file("resources/main/META-INF/mods.toml"))
    }
}

tasks.register<Delete>("buildCollectAndClean") {
    group = "build"

    delete(layout.buildDirectory.dir("libs"))
    delete(layout.buildDirectory.dir("devlibs"))

    dependsOn("buildAndCollect")
}

tasks.register<Delete>("deleteBuildCache") {
    group = "build"

    delete(layout.buildDirectory)
}