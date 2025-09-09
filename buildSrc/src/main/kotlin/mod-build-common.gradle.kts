@file:Suppress("unused")

enum class Loader(val formattedName: String) {
    FABRIC("Fabric"),
    FORGE("Forge"),
    NEOFORGE("NeoForge");

    fun id() = this.name.lowercase()

    companion object {
        fun of(id: String) = valueOf(id.uppercase())
    }
}

class ModDeps {
    val minecraft = project.property("deps.minecraft") as String
    val moonlight = project.property("deps.moonlight") as String

    val loader = Loader.of(project.property("deps.loader") as String)
    val isFabric = loader == Loader.FABRIC
    val isNeoforge = loader == Loader.NEOFORGE
    val isForge = loader == Loader.FORGE
    val isForgeLike = isNeoforge || isForge
}

class ModData {
    val version = project.property("mod.version") as String
    val group = project.property("mod.group") as String
    val id = project.property("mod.id") as String
    val name = project.property("mod.name") as String
    val authors = project.property("mod.authors") as String
    val description = project.property("mod.description") as String
    val homepage = project.property("mod.homepage") as String
    val sources = project.property("mod.sources") as String
    val issues = project.property("mod.issues") as String
    val license = project.property("mod.license") as String

    val deps = ModDeps()

    fun getProps(): Map<String, String> = mapOf(
        "mod_id" to this.id,
        "mod_name" to this.name,
        "mod_version" to "${this.version}+${this.deps.minecraft}-${this.deps.loader}",
        "mod_group" to this.group,
        "mod_author" to this.authors,
        "mod_license" to this.license,
        "mod_description" to this.description,
        "mod_homepage" to this.homepage,
        "mod_sources" to this.sources,
        "mod_issues" to this.issues,
        "mod_author_list" to this.authors.split(", ").joinToString("\",\""),
        "minecraft" to this.deps.minecraft,
        "moonlight" to this.deps.moonlight,
    )

    fun getStonecutterConfiguration(eval: (version: String, predicates: Array<String>) -> Boolean) = StonecutterConfiguration(this, eval)
    fun getFletchingTableConfiguration(eval: (version: String, predicates: Array<String>) -> Boolean) = FletchingTableConfiguration(this, eval)
}

fun <K: Any, V: Any>toImmutable(map: MutableMap<K, V>) = mapOf(*map.entries.map { entry -> entry.key to entry.value }.toTypedArray())
inline fun <reified K: Any>toImmutable(list: MutableList<K>) = listOf(*list.toTypedArray())

class StonecutterConfiguration {
    val constants: Map<String, Boolean>
    val swaps: Map<String, String>

    constructor(mod: ModData, eval: (version: String, predicates: Array<String>) -> Boolean) {
        val constants = mutableMapOf<String, Boolean>()
        val swaps = mutableMapOf<String, String>()

        val deps = mod.deps
        val is120 = eval(deps.minecraft, arrayOf("<1.21"))

        Loader.entries.forEach { loader -> constants[loader.id()] = deps.loader == loader }
        constants["forgeLike"] = deps.isForgeLike

        swaps["client_only"] = when {
            deps.isForge -> "@net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)"
            deps.isFabric -> "@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)"
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

        this.constants = toImmutable(constants)
        this.swaps = toImmutable(swaps)
    }
}

class FletchingTableConfiguration {
    val extensions: List<Pair<String, Array<String>>>

    constructor(mod: ModData, eval: (version: String, predicates: Array<String>) -> Boolean) {
        val extensions = mutableListOf<Pair<String, Array<String>>>()

        val deps = mod.deps
        val is120 = eval(deps.minecraft, arrayOf("<1.21"))

        if (is120) {
            extensions.add("json" to arrayOf(
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
            ))
        }

        this.extensions = toImmutable(extensions)
    }
}

interface ModCommon {
    val mod: Property<ModData>
}

val extension = project.extensions.create<ModCommon>("mod-common")
extension.mod.convention(ModData())
