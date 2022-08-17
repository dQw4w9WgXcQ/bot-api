package github.dqw4w9wgxcq.botapi.data

import github.dqw4w9wgxcq.botapi.coords.Areas
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint

object LocationData {
    val WILDERNESS_ABOVE_GROUND_AREA = WorldArea(2944, 3523, 448, 448, 0)
    val WILDERNESS_UNDERGROUND_AREA = WorldArea(2944, 9918, 320, 442, 0)
    val LUMBRIDGE_SPAWN_POINT = WorldPoint(3222, 3219, 0)//spawn
    val VARROCK_EAST_BANK_POINT = WorldPoint(3253, 3421, 0)
    val MULE_POINT = WorldPoint(3164, 3492, 0)
    val TAVERLEY_DUNGEON_AREA = Areas.World(2810, 9669, 2970, 9854, 0)
    val PISC_AREA = Areas.World(2264, 3539, 2406, 3652, 0)
    val FEROX_AREA = Areas.World(3122, 3617, 3156, 3646, 0)
    val EDGEVILLE_POINT = WorldPoint(3101, 3503, 0)
    val RC_ALTAR_AREA = Areas.World(2114, 4798, 2865, 4864, 0)
    val ZANARIS_AREA = Areas.World(2372, 4365, 2426, 4449, 0)

    val WIZARD_TOWER_CLOSED_DIAGONAL_DOOR_POINT = WorldPoint(3107, 3162, 0)
    val LUMBRIDGE_STAIR_TILE = WorldPoint(3204, 3207, 0)

    const val WILDERNESS_DITCH_Y = 3521

    const val LUMBRIDGE = 12850
    const val LUMBRIDGE_SWAMP_EAST = 12849
    const val LUMBRIDGE_SWAMP_WEST = 12593
    const val LUMBRIDGE_HAM_FOREST = 12594
    const val LUMBRIDGE_MILL = 12595
    const val LUMBRIDGE_CHICKEN_COW = 12851
    const val WIZARD_TOWER = 12337
    const val WIZARD_TOWER_BASEMENT = 12437
    const val GRAND_EXCHANGE = 12598
    const val VARROCK_SQUARE = 12853
    const val VARROCK_PALACE = 12854
    const val LUMBER_YARD = 13110
    const val DIGSITE = 13365
    const val EXAM_CENTRE = 13364
    const val EDGEVILLE = 12342
    const val DRAYNOR = 12338
    const val DRAYNOR_NORTH = 12339
    const val DRAYNOR_MANOR = 12340
    const val PORT_SARIM = 12082
    const val AL_KHARID = 13105
    const val AL_KHARID_NORTH = 13106//toll gate, duel arena tele,fire altar, gnome glider, farming patch
    const val AL_KHARID_MINE = 13107
    const val CLAN_WARS = 13361
    const val DUEL_ARENA_LOBBY_BANK = 13362
    const val DUEL_ARENA_FIGHT = 13363
    const val SHANTAY_PASS = 13104
    const val FALADOR = 11828
    const val FALADOR_EAST = 12084
    const val FALADOR_FARM = 12083
    const val FALADOR_MINE_DARK_TOWER = 11572
    const val FALADOR_SOUTH = 11827//chainbody shop, east of crafting guild, west of farm
    const val CRAFTING_GUILD = 11571//also contains makeover mage
    const val MELZARS_MAZE = 11570//contains docks to corsair cove
    const val TAVERLY_NORTH = 11574
    const val TAVERLY_SOUTH = 11573
    const val WHITE_WOLF_MOUNTAIN = 11318
    const val CATHERBY = 11061
    const val CATHERBY_EAST = 11317
    const val CAMELOT = 11062//part of the castle is in seers
    const val RELLEKKA = 10553
    const val RELLEKKA_WEST = 10297
    const val BARBARIAN_ASSAULT_LOBBY = 10039
    const val BAXTORIAN_FALLS = 10038
    const val GLARIALS_TOMB = 10037
    const val GRAND_TREE = 9782
    const val TREE_GNOME_STRONGHOLD = 9781
    const val OUTPOST = 9780
    const val SEERS_VILLAGE = 10806
    const val MCGRUBORS_WOOD = 10550
    const val RANGING_GUILD = 10549
    const val FISHING_GUILD = 10293
    const val ARDOUGNE = 10547
    const val ARDOUGNE_EAST_CASTLE = 10291
    const val ARDOUGNE_WEST = 10035
    const val CASTLE_WARS_LOBBY = 9776
    const val YANILLE = 10288
    const val RIMMINGTON = 11826
    const val MONESTARY = 12086
    const val BARBARIAN_VILLAGE = 12341
    const val CHAMPIONS_GUILD_MINE = 12596
    const val SOUTH_VARROCK_DARK_WIZARD = 12852
    const val VARROCK_EAST_MINE = 13108
    const val DORIC = 11829
    const val BURTHORPE = 11575
    const val WARRIORS_GUILD = 11319
    const val FEROX_EAST = 12600
    const val FEROX_WEST = 12344
    const val EDGEVILLE_WILDERNESS = 12343
    const val BRIMHAVEN = 11057
    const val BRIMHAVEN_DOCKS = 11058
    const val KARAMJA_VOLCANO = 11313
    const val MUSA_POINT = 11569
    const val TAI_BWO_WANNAI_NORTH = 11056
    const val KARAMJA_DUNGEON_ENTRANCE = 11312
    const val CAIRN_ISLE = 11054
    const val SHILO_VILLAGE = 11310
    const val TAI_BWO_WANNAI = 11055
    const val KALPHITE_ENTRANCE = 12848
    const val DESERT_MINING_CAMP = 13103
    const val POLLNIVNEACH = 13358
    const val UZER = 13872
    const val DT_PYRAMID = 12845
    const val SOPHANEM = 13099
    const val TEMPOROSS_DOCKS = 12588
    const val QUARRY = 12589
    const val BANDIT_CAMP = 12590
    const val BEDABIN = 12591
    const val PRIEST_IN_PERIL_TEMPLE = 13622
    const val CANIFIS = 13878
    const val MORTTON = 13875
    const val BARROWS = 14131
    const val BURGH_DE_ROTT = 13874
    const val CORSAIR_COVE = 10284
    const val CORSAIR_COVE_WEST = 10028
    const val CORSAIR_DUNGEON = 8076
    const val CORSAIR_DUNGEON_EAST = 8332

    //zeah
    const val PISCARILIUS_DOCKS = 7225
    const val KOUREND_CASTLE = 6457
    const val KOUREND_CASTLE_EAST = 6713
    const val ARCEUUS = 6714
    const val ARCEUUS_LIBRARY = 6459
    const val WINTERTODT_LOBBY = 6461
    const val WINTERTODT = 6462
    const val WOODCUTTING_GUILD_WEST = 6198
    const val WOODCUTTING_GUILD_EAST = 6454//includes tele
    const val CRABCLAW = 6965
    const val SALTPETRE_MINE = 6711
    const val LANDS_END = 5941
    const val KOUREND_WOODLAND = 5942
    const val KOUREND_WOODLAND_BARBARIAN = 6197

    //underground
    const val LUMBRIDGE_BASEMENT = 12950
    const val EDGEVILLE_DUNGEON_MID = 12442
    const val EDGEVILLE_DUNGEON_WILDERNESS = 12443//part of wild is in 12442
    const val EDGEVILLE_DUNGEON_HILL_GIANT = 12441
    const val VARROCK_SEWERS = 12954
    const val DRAYNOR_MANOR_UNDERGROUND = 12440

    const val GOBLIN_CAVE = 10393
    const val TEMPLE_OF_IKOV_NORTH = 10650
    const val TEMPLE_OF_IKOV_SOUTH = 10649
    const val BOOTS_OF_LIGHTNESS = 10648
    const val ROGUES_DEN_LOBBY = 12109//door

    //special areas
    const val FFA_PORTAL = 13130
    const val VARROCK_MUSEUM = 6989
    const val COSMIC_ALTAR = 8523
    const val CHAOS_ALTAR = 9035
    const val NATURE_ALTAR = 9547
    const val FIRE_ALTAR = 10315
    const val BODY_ALTAR = 10059
    const val EARTH_ALTAR = 10571
    const val ESSENCE_MINE = 11595
    const val ABYSS = 12107
    const val ZANARIS_BANK = 9541
    const val ZANARIS_COSMIC_TANGLEROOT = 9540
    const val ZANARIS_EAST = 9797
}
