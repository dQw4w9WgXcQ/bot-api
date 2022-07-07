package github.dqw4w9wgxcq.botapi.tabs.magic

interface Spell {
    val level: Int
    val widgetName: String

    enum class Modern(override val level: Int, override val widgetName: String) : Spell {
        LUMBRIDGE_HOME_TELEPORT(0, "Lumbridge Home Teleport"),
        WIND_STRIKE(1, "Wind Strike"),
        CONFUSE(3, "Confuse"),
        WATER_STRIKE(5, "Water Strike"),
        LVL_1_ENCHANT(7, "Lvl-1 Enchant"),
        EARTH_STRIKE(9, "Earth Strike"),
        WEAKEN(11, "Weaken"),
        FIRE_STRIKE(13, "Fire Strike"),
        CURSE(19, "Curse"),
        LOW_LEVEL_ALCHEMY(21, "Low Level Alchemy"),
        VARROCK_TELEPORT(25, "Varrock Teleport"),
        LUMBRIDGE_TELEPORT(31, "Lumbridge Teleport"),
        TELEKINETIC_GRAB(33, "Telekinetic Grab"),
        FALADOR_TELEPORT(37, "Falador Teleport"),
        CAMELOT_TELEPORT(45, "Camelot Teleport"),
        HIGH_LEVEL_ALCHEMY(55, "High Level Alchemy"),
    }

    enum class Arceuus(override val level: Int, override val widgetName: String) : Spell {
        SALVE_GRAVEYARD_TELEPORT(40, "Salve Graveyard Teleport"),
    }
}
