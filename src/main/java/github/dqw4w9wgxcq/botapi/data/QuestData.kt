package github.dqw4w9wgxcq.botapi.data

import github.dqw4w9wgxcq.botapi.varps.Varps

enum class QuestData(val id: Int, val isVarbit: Boolean) {
    BLACK_KNIGHTS_FORTRESS(130, false),
    COOKS_ASSISTANT(29, false),
    DEMON_SLAYER(2561, true),
    DORICS_QUEST(31, false),
    DRAGON_SLAYER_I(176, false),
    ERNEST_THE_CHICKEN(32, false),
    GOBLIN_DIPLOMACY(2378, true),
    IMP_CATCHER(160, false),
    THE_KNIGHTS_SWORD(122, false),
    PIRATES_TREASURE(71, false),
    PRINCE_ALI_RESCUE(273, false),
    THE_RESTLESS_GHOST(107, false),
    ROMEO_JULIET(144, false),
    RUNE_MYSTERIES(63, false),
    SHEEP_SHEARER(179, false),
    VAMPYRE_SLAYER(178, false),
    WITCHS_POTION(67, false),
    MISTHALIN_MYSTERY(3468, true),
    THE_CORSAIR_CURSE(6071, true),
    ANIMAL_MAGNETISM(3185, true),
    BETWEEN_A_ROCK(299, true),
    BIG_CHOMPY_BIRD_HUNTING(293, false),
    BIOHAZARD(68, false),
    CABIN_FEVER(655, false),
    CLOCK_TOWER(10, false),
    CONTACT(3274, true),
    ZOGRE_FLESH_EATERS(487, true),
    CREATURE_OF_FENKENSTRAIN(399, false),
    DARKNESS_OF_HALLOWVALE(2573, true),
    DEATH_TO_THE_DORGESHUUN(2258, true),
    DEATH_PLATEAU(314, false),
    DESERT_TREASURE(358, true),
    DEVIOUS_MINDS(1465, true),
    THE_DIG_SITE(131, false),
    DRUIDIC_RITUAL(80, false),
    DWARF_CANNON(0, false),
    EADGARS_RUSE(335, false),
    EAGLES_PEAK(2780, true),
    ELEMENTAL_WORKSHOP_II(2639, true),
    ENAKHRAS_LAMENT(1560, true),
    ENLIGHTENED_JOURNEY(2866, true),
    THE_EYES_OF_GLOUPHRIE(2497, true),
    FAIRYTALE_I_GROWING_PAINS(1803, true),
    FAIRYTALE_II_CURE_A_QUEEN(2326, true),
    FAMILY_CREST(148, false),
    THE_FEUD(334, true),
    FIGHT_ARENA(17, false),
    FISHING_CONTEST(11, false),
    FORGETTABLE_TALE(822, true),
    THE_FREMENNIK_TRIALS(347, false),
    WATERFALL_QUEST(65, false),
    GARDEN_OF_TRANQUILLITY(961, true),
    GERTRUDES_CAT(180, false),
    GHOSTS_AHOY(217, true),
    THE_GIANT_DWARF(571, true),
    THE_GOLEM(346, true),
    THE_GRAND_TREE(150, false),
    THE_HAND_IN_THE_SAND(1527, true),
    HAUNTED_MINE(382, false),
    HAZEEL_CULT(223, false),
    HEROES_QUEST(188, false),
    HOLY_GRAIL(5, false),
    HORROR_FROM_THE_DEEP(34, true),
    ICTHLARINS_LITTLE_HELPER(418, true),
    IN_AID_OF_THE_MYREQUE(1990, true),
    IN_SEARCH_OF_THE_MYREQUE(387, false),
    JUNGLE_POTION(175, false),
    LEGENDS_QUEST(139, false),
    LOST_CITY(147, false),
    THE_LOST_TRIBE(532, true),
    LUNAR_DIPLOMACY(2448, true),
    MAKING_HISTORY(1383, true),
    MERLINS_CRYSTAL(14, false),
    MONKEY_MADNESS_I(365, false),
    MONKS_FRIEND(30, false),
    MOUNTAIN_DAUGHTER(260, true),
    MOURNINGS_END_PART_I(517, false),
    MOURNINGS_END_PART_II(1103, true),
    MURDER_MYSTERY(192, false),
    MY_ARMS_BIG_ADVENTURE(2790, true),
    NATURE_SPIRIT(307, false),
    OBSERVATORY_QUEST(112, false),
    ONE_SMALL_FAVOUR(416, false),
    PLAGUE_CITY(165, false),
    PRIEST_IN_PERIL(302, false),
    RAG_AND_BONE_MAN_I(714, false),
    RATCATCHERS(1404, true),
    RECIPE_FOR_DISASTER(1850, true),
    RECRUITMENT_DRIVE(657, true),
    REGICIDE(328, false),
    ROVING_ELVES(402, false),
    ROYAL_TROUBLE(2140, true),
    RUM_DEAL(600, false),
    SCORPION_CATCHER(76, false),
    SEA_SLUG(159, false),
    THE_SLUG_MENACE(2610, true),
    SHADES_OF_MORTTON(339, false),
    SHADOW_OF_THE_STORM(1372, true),
    SHEEP_HERDER(60, false),
    SHILO_VILLAGE(116, false),
    A_SOULS_BANE(2011, true),
    SPIRITS_OF_THE_ELID(1444, true),
    SWAN_SONG(2098, true),
    TAI_BWO_WANNAI_TRIO(320, false),
    A_TAIL_OF_TWO_CATS(1028, true),
    TEARS_OF_GUTHIX(451, true),
    TEMPLE_OF_IKOV(26, false),
    THRONE_OF_MISCELLANIA(359, false),
    THE_TOURIST_TRAP(197, false),
    WITCHS_HOUSE(226, false),
    TREE_GNOME_VILLAGE(111, false),
    TRIBAL_TOTEM(200, false),
    TROLL_ROMANCE(385, false),
    TROLL_STRONGHOLD(317, false),
    UNDERGROUND_PASS(161, false),
    WANTED(1051, true),
    WATCHTOWER(212, false),
    COLD_WAR(3293, true),
    THE_FREMENNIK_ISLES(3311, true),
    TOWER_OF_LIFE(3337, true),
    THE_GREAT_BRAIN_ROBBERY(980, false),
    WHAT_LIES_BELOW(3523, true),
    OLAFS_QUEST(3534, true),
    ANOTHER_SLICE_OF_H_A_M(3550, true),
    DREAM_MENTOR(3618, true),
    GRIM_TALES(2783, true),
    KINGS_RANSOM(3888, true),
    MONKEY_MADNESS_II(5027, true),
    CLIENT_OF_KOUREND(5619, true),
    RAG_AND_BONE_MAN_II(714, false),
    BONE_VOYAGE(5795, true),
    THE_QUEEN_OF_THIEVES(6037, true),
    THE_DEPTHS_OF_DESPAIR(6027, true),
    DRAGON_SLAYER_II(6104, true),
    TALE_OF_THE_RIGHTEOUS(6358, true),
    A_TASTE_OF_HOPE(6396, true),
    MAKING_FRIENDS_WITH_MY_ARM(6528, true),
    THE_FORSAKEN_TOWER(7796, true),
    THE_ASCENT_OF_ARCEUUS(7856, true),
    ENTER_THE_ABYSS(492, false),
    ARCHITECTURAL_ALLIANCE(4982, true),
    BEAR_YOUR_SOUL(5078, true),
    ALFRED_GRIMHANDS_BARCRAWL(77, false),
    THE_GENERALS_SHADOW(3330, true),
    IN_SEARCH_OF_KNOWLEDGE(8403, true),
    SKIPPY_AND_THE_MOGRES(1344, true),
    MAGE_ARENA_I(267, false),
    LAIR_OF_TARN_RAZORLOR(3290, true),
    FAMILY_PEST(5347, true),
    MAGE_ARENA_II(6067, true),
    DADDYS_HOME(10570, true),
    X_MARKS_THE_SPOT(8063, true),
    SONG_OF_THE_ELVES(9016, true),
    THE_FREMENNIK_EXILES(9459, true),
    SINS_OF_THE_FATHER(7255, true),
    A_PORCINE_OF_INTEREST(10582, true),
    GETTING_AHEAD(693, true),
    BELOW_ICE_MOUNTAIN(12063, true),
    A_NIGHT_AT_THE_THEATRE(12276, true),
    A_KINGDOM_DIVIDED(12296, true),
    TEMPLE_OF_THE_EYE(13738, true),
    ;

    companion object {
        fun getProgress(quest: QuestData): Int {
            return if (quest.isVarbit) {
                Varps.getBit(quest.id)
            } else {
                Varps.get(quest.id)
            }
        }
    }

    fun getRl(): net.runelite.api.Quest {
        return net.runelite.api.Quest.valueOf(name)
    }
}

//generated from runescript https://github.com/RuneStar/cs2-scripts/blob/b0eb330380e04db807620c234c486dc2e1bc7ac3/scripts/%5Bproc%2Cquest_progress_get%5D.cs2

fun main() {
    val string = """"""

    val lines = string.lines()

    val sourceLines = mutableListOf<String>()

    for (line in lines) {
        val name = line.substringBeforeLast("_")
        val config = line.substringAfter(":")
        println(name)
        println(config)

        var sourceLine = name.uppercase()
        sourceLine += "("
        sourceLine += if (config.contains("varbit")) {
            config.removePrefix("varbit") +", true"
        } else {
            config.removePrefix("var") +", false"
        }
        sourceLine += "),"

        sourceLines.add(sourceLine)
    }

    for (sourceLine in sourceLines) {
        println(sourceLine)
    }
}
