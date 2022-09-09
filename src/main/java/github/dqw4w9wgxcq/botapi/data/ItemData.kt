package github.dqw4w9wgxcq.botapi.data

import github.dqw4w9wgxcq.botapi.Skills
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.wrappers.Nameable
import github.dqw4w9wgxcq.botapi.wrappers.item.Item
import github.dqw4w9wgxcq.botapi.wrappers.item.container.ContainerItem
import github.dqw4w9wgxcq.botapi.wrappers.item.container.InventoryItem
import net.runelite.api.EquipmentInventorySlot
import net.runelite.api.ItemID
import net.runelite.api.Skill

object ItemData {
    val COMBAT_BRACELET: (Item) -> Boolean = byPrefix("combat bracelet(")
    val SKILLS_NECKLACE: (Item) -> Boolean = byPrefix("skills necklace(")
    val GAMES_NECKLACE: (Item) -> Boolean = byPrefix("games necklace(")
    val RING_OF_WEALTH: (Item) -> Boolean = byPrefix("ring of wealth (")
        .and<Item> { it.id != ItemID.RING_OF_WEALTH_I }
        .withDescription("RING_OF_WEALTH")
    val RING_OF_DUELING: (Item) -> Boolean = byPrefix("ring of dueling(")
    val AMULET_OF_GLORY: (Item) -> Boolean = byPrefix("amulet of glory(")
        .and<Item> { it.id != ItemID.AMULET_OF_GLORY_T }
        .withDescription("AMULET_OF_GLORY")
    val NECKLACE_OF_PASSAGE: (Item) -> Boolean = byPrefix("necklace of passage(")
    val DIGSITE_PENDANT: (Item) -> Boolean = byPrefix("Digsite pendant(")
    val SLAYER_RING: (Item) -> Boolean = byPrefix("Slayer ring (")
    val BURNING_AMULET: (Item) -> Boolean = byPrefix("Burning amulet(")
    val STAMINA_POTION: (Item) -> Boolean = byPrefix("stamina potion(")
    val ENERGY_POTION: (Item) -> Boolean = byPrefix("energy potion(")
    val EQUIPABLE = byAction("equip", "wield", "wear")
    val FOOD = byAction("Eat")
    val BOND = byPrefix("old school bond")
    val GRACEFUL = byPrefix("graceful ")
    val THIEVING_CAPE = byId(ItemID.THIEVING_CAPE, ItemID.THIEVING_CAPET)
    val BIRD_NEST_FILLED: (Item) -> Boolean = byName("bird nest").and { it.id != ItemID.BIRD_NEST }
    val BOOTS_OF_LIGHTNESS: (Item) -> Boolean = byId(ItemID.BOOTS_OF_LIGHTNESS, ItemID.BOOTS_OF_LIGHTNESS_89)
    val SUMMER_PIE: (Item) -> Boolean = byId(ItemID.SUMMER_PIE, ItemID.HALF_A_SUMMER_PIE)
    val STRENGTH_POTION: (Item) -> Boolean = byPrefix("Strength potion(")

    val NOT_NOTED: (ContainerItem) -> Boolean = { it !is InventoryItem || !it.isNoted }

    //as of 7/20/21
    val F2P_TRADE_RESTRICTED_ITEMS = setOf(
        ItemID.OAK_LOGS,
        ItemID.WILLOW_LOGS,
        ItemID.YEW_LOGS,
        ItemID.RAW_SHRIMPS,
        ItemID.SHRIMPS,
        ItemID.RAW_ANCHOVIES,
        ItemID.ANCHOVIES,
        ItemID.RAW_LOBSTER,
        ItemID.LOBSTER,
        ItemID.CLAY,
        ItemID.SOFT_CLAY,
        ItemID.COPPER_ORE,
        ItemID.TIN_ORE,
        ItemID.IRON_ORE,
        ItemID.SILVER_ORE,
        ItemID.GOLD_ORE,
        ItemID.COAL,
        ItemID.MITHRIL_ORE,
        ItemID.ADAMANTITE_ORE,
        ItemID.RUNITE_ORE,
        ItemID.COWHIDE,
        ItemID.VIAL,
        ItemID.VIAL_OF_WATER,
        ItemID.JUG_OF_WATER,
        ItemID.FISHING_BAIT,
        ItemID.FEATHER,
        ItemID.EYE_OF_NEWT,
        ItemID.WINE_OF_ZAMORAK,
        ItemID.AIR_RUNE,
        ItemID.WATER_RUNE,
        ItemID.EARTH_RUNE,
        ItemID.FIRE_RUNE,
        ItemID.MIND_RUNE,
        ItemID.CHAOS_RUNE,
    )

    val F2P_TRADE_RESTRICTED = byIdIgnoreNote(*F2P_TRADE_RESTRICTED_ITEMS.toIntArray())
//    fun getCharges(nameable: Nameable):Int?{
//        return
//    }

    fun charges(charges: Int): (Nameable) -> Boolean {
        return { it.name.endsWith("($charges)") }
    }

    val NOT_DIGIT = "\\D".toRegex()
    fun chargeAtLeast(min: Int): (Nameable) -> Boolean {
        return {
            var name = it.name
            name = name.substring(name.indexOf(")") - 2)
            debug { name }
            name.replace(NOT_DIGIT, "").toInt() >= min
        }
    }

    val ROGUE_OUTFIT = mapOf(
        EquipmentInventorySlot.HEAD to ItemID.ROGUE_MASK,
        EquipmentInventorySlot.BODY to ItemID.ROGUE_TOP,
        EquipmentInventorySlot.LEGS to ItemID.ROGUE_TROUSERS,
        EquipmentInventorySlot.GLOVES to ItemID.ROGUE_GLOVES,
        EquipmentInventorySlot.BOOTS to ItemID.ROGUE_BOOTS,
    )

    enum class Graceful(
        val itemName: String,
        val marksCost: Int,
        val id: Int,
        val slot: EquipmentInventorySlot,
    ) : (Item) -> Boolean {
        HOOD(
            "Graceful hood",
            35,
            ItemID.GRACEFUL_HOOD,
            EquipmentInventorySlot.HEAD
        ),
        CAPE(
            "Graceful cape",
            40,
            ItemID.GRACEFUL_CAPE,
            EquipmentInventorySlot.CAPE
        ),
        GLOVES(
            "Graceful gloves",
            30,
            ItemID.GRACEFUL_GLOVES,
            EquipmentInventorySlot.GLOVES
        ),
        TOP(
            "Graceful top",
            55,
            ItemID.GRACEFUL_TOP,
            EquipmentInventorySlot.BODY
        ),
        LEGS(
            "Graceful legs",
            60,
            ItemID.GRACEFUL_LEGS,
            EquipmentInventorySlot.LEGS
        ),
        BOOTS(
            "Graceful boots",
            40,
            ItemID.GRACEFUL_BOOTS,
            EquipmentInventorySlot.BOOTS
        ),
        ;

        override fun invoke(item: Item): Boolean {
            return item.name == itemName
        }
    }

    enum class EssPouch(
        val id: Int,
        val degradedIds: Set<Int>,
        val capacity: Int,
        val durability: Int,
        val level: Int,
    ) : (Item) -> Boolean {
        SMALL(
            ItemID.SMALL_POUCH,
            emptySet(),
            3,
            Int.MAX_VALUE,
            1
        ),
        MEDIUM(
            ItemID.MEDIUM_POUCH,
            setOf(ItemID.MEDIUM_POUCH_5511),
            6,
            45,
            25
        ),
        LARGE(
            ItemID.LARGE_POUCH,
            setOf(ItemID.LARGE_POUCH_6819, ItemID.LARGE_POUCH_5513),
            9,
            29,
            50
        ),
        GIANT(
            ItemID.GIANT_POUCH,
            setOf(ItemID.GIANT_POUCH_5515),
            12,
            11,
            75
        ),
        ;

        override fun invoke(item: Item): Boolean {//matches
            val itemId = item.id
            return id == itemId || degradedIds.contains(itemId)
        }

        companion object {
            val degradedMatches = byId(*values().flatMap { it.degradedIds }.toIntArray())

            fun pouchesForLevel(lvl: Int = Skills.level(Skill.RUNECRAFT)): List<EssPouch> {
                require(lvl >= 1)
                val lastPouchIndex = values().indexOfLast { it.level <= lvl }

                return values().toList().subList(0, lastPouchIndex)
            }
        }
    }

//    private val itemNameRegex = "[^A-Z0-9 ]".toRegex()
//    fun idFromName(itemName: String): Int {
//        val fieldName = itemName.uppercase().replace(itemNameRegex, "").replace(" ", "_")
//
//        val field = ItemID::class.java.getDeclaredField(fieldName)
//            ?: throw IllegalArgumentException("no ItemID found for: $itemName, formatted: $fieldName")
//
//        return field.getInt(null)
//    }
}
