package github.dqw4w9wgxcq.botapi

import github.dqw4w9wgxcq.botapi.antiban.Profile
import github.dqw4w9wgxcq.botapi.sceneentities.actors.players.Players
import github.dqw4w9wgxcq.botapi.skill.Skills
import github.dqw4w9wgxcq.botapi.worlds.Worlds
import net.runelite.api.EquipmentInventorySlot
import net.runelite.api.ItemID
import net.runelite.api.Skill

object EquipmentSets {
    fun melee(
        att: Int = Skills.level(Skill.ATTACK),
        def: Int = Skills.level(Skill.DEFENCE),
        p2p: Boolean = Worlds.onP2p(),
    ): MutableMap<EquipmentInventorySlot, Int> {
        return buildMap {
            putAll(meleeArmor(def, p2p))

            put(
                EquipmentInventorySlot.WEAPON,
                when {
                    att < 5 -> ItemID.IRON_SCIMITAR
                    att < 20 -> ItemID.STEEL_SCIMITAR
                    att < 30 -> ItemID.MITHRIL_SCIMITAR
                    att < 40 -> ItemID.ADAMANT_SCIMITAR
                    att < 60 || !p2p -> ItemID.RUNE_SCIMITAR
                    att < 70 -> ItemID.DRAGON_SWORD
                    else -> {
                        remove(EquipmentInventorySlot.SHIELD)
                        ItemID.SARADOMIN_SWORD
                    }
                }
            )
        }.toMutableMap()
    }

    fun ranged(): MutableMap<EquipmentInventorySlot, Int> {
        return buildMap {
            put(EquipmentInventorySlot.AMULET, ItemID.AMULET_OF_POWER)

            val rangedLevel = Skills.level(Skill.RANGED)
            val defLevel = Skills.level(Skill.DEFENCE)

            put(
                EquipmentInventorySlot.WEAPON,
                when {
                    rangedLevel < 5 -> ItemID.SHORTBOW
                    rangedLevel < 20 -> ItemID.OAK_SHORTBOW
                    rangedLevel < 30 -> ItemID.WILLOW_SHORTBOW
                    else -> ItemID.MAPLE_SHORTBOW
                }
            )

            put(
                EquipmentInventorySlot.HEAD,
                when {
                    rangedLevel < 20 -> ItemID.LEATHER_COWL
                    else -> ItemID.COIF
                }
            )

            put(
                EquipmentInventorySlot.BODY,
                when {
                    defLevel < 10 -> ItemID.LEATHER_BODY
                    else -> ItemID.HARDLEATHER_BODY// studded body is low trade volume.  has same ranged offense bonus
                }
            )

            put(
                EquipmentInventorySlot.LEGS,
                when {
                    rangedLevel < 40 -> ItemID.LEATHER_CHAPS
                    //rangedLevel < 40 -> ItemID.STUDDED_CHAPS//trade volume low
                    else -> ItemID.GREEN_DHIDE_CHAPS
                }
            )

            put(
                EquipmentInventorySlot.GLOVES,
                when {
                    rangedLevel < 40 -> ItemID.LEATHER_VAMBRACES
                    else -> ItemID.GREEN_DHIDE_VAMBRACES
                }
            )
        }.toMutableMap()
    }

    fun magic(magicLvl: Int = Skills.level(Skill.MAGIC)): MutableMap<EquipmentInventorySlot, Int> {
        return buildMap {
            put(EquipmentInventorySlot.AMULET, ItemID.AMULET_OF_MAGIC)

            put(
                EquipmentInventorySlot.HEAD,
                if (Profile.getBoolean("wizard hat")) ItemID.WIZARD_HAT else ItemID.BLUE_WIZARD_HAT
            )
            put(
                EquipmentInventorySlot.BODY,
                if (Profile.getBoolean("wizard robe")) ItemID.BLACK_ROBE else ItemID.BLUE_WIZARD_ROBE
            )
            put(EquipmentInventorySlot.WEAPON, if (magicLvl < 13) ItemID.STAFF_OF_AIR else ItemID.STAFF_OF_FIRE)
            val legs = when (Profile.getInt("f2p magic legs", 30)) {
                0 -> ItemID.LEATHER_CHAPS
                1 -> ItemID.BLUE_SKIRT
                2 -> ItemID.PINK_SKIRT
                3 -> ItemID.MONKS_ROBE
                4 -> ItemID.BLACK_SKIRT
                5 -> ItemID.PRIEST_GOWN_428
                in 15 until 30 -> ItemID.ZAMORAK_MONK_BOTTOM
                else -> null
            }
            if (legs != null) {
                put(EquipmentInventorySlot.LEGS, legs)
            }
        }.toMutableMap()
    }

    fun meleeArmor(
        def: Int = Skills.level(Skill.DEFENCE),
        p2p: Boolean = Worlds.onP2p(),
    ): MutableMap<EquipmentInventorySlot, Int> {
        val plateskirt = Profile.getBoolean("plateskirt") && Players.local().isFemale

        return buildMap {
            putAll(accessories())
            put(EquipmentInventorySlot.AMULET, ItemID.AMULET_OF_STRENGTH)

            //armor
            put(EquipmentInventorySlot.HEAD, ItemID.IRON_FULL_HELM)
            put(EquipmentInventorySlot.BODY, ItemID.IRON_CHAINBODY)
            put(EquipmentInventorySlot.LEGS, ItemID.IRON_PLATELEGS)
            put(EquipmentInventorySlot.SHIELD, ItemID.IRON_KITESHIELD)

            if (def >= 5) {
                put(EquipmentInventorySlot.HEAD, ItemID.STEEL_FULL_HELM)
                put(EquipmentInventorySlot.BODY, ItemID.STEEL_PLATEBODY)
                put(EquipmentInventorySlot.LEGS, ItemID.STEEL_PLATELEGS)
                put(EquipmentInventorySlot.SHIELD, ItemID.STEEL_KITESHIELD)
            }

            if (def >= 20) {
                put(EquipmentInventorySlot.HEAD, ItemID.MITHRIL_FULL_HELM)
                put(EquipmentInventorySlot.BODY, ItemID.MITHRIL_PLATEBODY)
                put(EquipmentInventorySlot.LEGS, ItemID.MITHRIL_PLATELEGS)
                put(EquipmentInventorySlot.SHIELD, ItemID.MITHRIL_KITESHIELD)
            }

            if (def >= 30) {
                put(EquipmentInventorySlot.HEAD, ItemID.ADAMANT_FULL_HELM)
                put(EquipmentInventorySlot.BODY, ItemID.ADAMANT_PLATEBODY)
                put(EquipmentInventorySlot.LEGS, ItemID.ADAMANT_PLATELEGS)//plateskirt low trade volume
                put(EquipmentInventorySlot.SHIELD, ItemID.ADAMANT_KITESHIELD)
                if (p2p) {
                    put(EquipmentInventorySlot.BOOTS, ItemID.ADAMANT_BOOTS)
                }
            }

            if (def >= 40) {
                put(EquipmentInventorySlot.HEAD, ItemID.RUNE_FULL_HELM)
                put(EquipmentInventorySlot.BODY, ItemID.RUNE_CHAINBODY)
                put(
                    EquipmentInventorySlot.LEGS,
                    if (plateskirt) ItemID.RUNE_PLATESKIRT else ItemID.RUNE_PLATELEGS
                )
                put(EquipmentInventorySlot.SHIELD, ItemID.RUNE_KITESHIELD)
                if (p2p) {
                    put(EquipmentInventorySlot.BOOTS, ItemID.RUNE_BOOTS)
                }
            }

            if (p2p) {
                put(EquipmentInventorySlot.GLOVES, ItemID.COMBAT_BRACELET6)

                if (def >= 60) {
                    put(EquipmentInventorySlot.HEAD, ItemID.DRAGON_MED_HELM)
                    put(
                        EquipmentInventorySlot.LEGS,
                        if (plateskirt) ItemID.DRAGON_PLATESKIRT else ItemID.DRAGON_PLATELEGS
                    )
                    put(EquipmentInventorySlot.SHIELD, ItemID.TOKTZKETXIL)
                    put(EquipmentInventorySlot.BOOTS, ItemID.DRAGON_BOOTS)
                }
            }
        }.toMutableMap()
    }

    fun accessories(): MutableMap<EquipmentInventorySlot, Int> {
        return buildMap {
            put(EquipmentInventorySlot.CAPE, Profile.pick("f2p cape", F2P_CAPES))

            if (Profile.getBoolean("should f2p glove")) {
                put(EquipmentInventorySlot.GLOVES, Profile.pick("f2p gloves", F2P_GLOVES))
            }

            if (Profile.getBoolean("f2p boot")) {
                put(EquipmentInventorySlot.BOOTS, ItemID.LEATHER_BOOTS)
            }
        }.toMutableMap()
    }

    fun casual(): MutableMap<EquipmentInventorySlot, Int> {
        return buildMap {
            val hat = when (Profile.getInt("hat", 6)) {
                0 -> ItemID.CHEFS_HAT
                1 -> ranged()[EquipmentInventorySlot.HEAD]!!
                else -> magic()[EquipmentInventorySlot.HEAD]!!
            }
            set(EquipmentInventorySlot.HEAD, hat)

            val body = when (Profile.getInt("body", 10)) {
                0 -> ItemID.BROWN_APRON
                1 -> ItemID.WHITE_APRON
                2 -> ItemID.MONKS_ROBE_TOP
                else -> magic()[EquipmentInventorySlot.BODY]!!
            }
            set(EquipmentInventorySlot.BODY, body)

            if (Profile.getBoolean("legs")) {
                val magicLegs = magic()[EquipmentInventorySlot.LEGS]
                if (magicLegs != null) {
                    set(EquipmentInventorySlot.LEGS, magicLegs)
                }
            }

            val weapon = when (Profile.getInt("casual weapon", 4)) {
                0 -> melee()[EquipmentInventorySlot.WEAPON]
                1 -> if (Profile.getBoolean("casual staff")) ItemID.STAFF_OF_FIRE else ItemID.STAFF_OF_AIR
                2 -> ranged()[EquipmentInventorySlot.WEAPON]
                else -> null
            }
            if (weapon != null) {
                set(EquipmentInventorySlot.WEAPON, weapon)
            }

            putAll(accessories())
        }.toMutableMap()
    }

    val F2P_CAPES = listOf(
//        ItemID.PURPLE_CAPE,
        ItemID.RED_CAPE,
//        ItemID.BLACK_CAPE,
        ItemID.BLUE_CAPE,
//        ItemID.GREEN_CAPE,
//        ItemID.ORANGE_CAPE,
//        ItemID.YELLOW_CAPE,
        ItemID.TEAM10_CAPE,
//        ItemID.TEAM20_CAPE,
        ItemID.TEAM30_CAPE,
//        ItemID.TEAM40_CAPE,
        ItemID.TEAM50_CAPE,
        ItemID.TEAM16_CAPE,
        ItemID.TEAM26_CAPE,
        ItemID.TEAM36_CAPE,
        ItemID.TEAM46_CAPE,
//        ItemID.TEAM6_CAPE,
//        ItemID.TEAM17_CAPE,
//        ItemID.TEAM27_CAPE,
//        ItemID.TEAM37_CAPE,
//        ItemID.TEAM47_CAPE,
//        ItemID.TEAM7_CAPE,
//        ItemID.TEAM18_CAPE,
//        ItemID.TEAM28_CAPE,
//        ItemID.TEAM38_CAPE,
//        ItemID.TEAM48_CAPE,
//        ItemID.TEAM8_CAPE,
    )

    val F2P_GLOVES = listOf(
        ItemID.LEATHER_GLOVES,
        ItemID.LEATHER_VAMBRACES,
//        ItemID.GREY_GLOVES,
        ItemID.PURPLE_GLOVES,
        ItemID.RED_GLOVES,
//        ItemID.TEAL_GLOVES,
        ItemID.YELLOW_GLOVES,
    )
}
