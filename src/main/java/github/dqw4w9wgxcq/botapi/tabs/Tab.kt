package github.dqw4w9wgxcq.botapi.tabs

import net.runelite.api.widgets.WidgetInfo

enum class Tab(val fixedId: Int, val varcInt: Int) {
    COMBAT(WidgetInfo.FIXED_VIEWPORT_COMBAT_TAB.childId, 0),
    SKILLS(WidgetInfo.FIXED_VIEWPORT_STATS_TAB.childId, 1),
    QUESTS(WidgetInfo.FIXED_VIEWPORT_QUESTS_TAB.childId, 2),
    INVENTORY(WidgetInfo.FIXED_VIEWPORT_INVENTORY_TAB.childId, 3),
    EQUIPMENT(WidgetInfo.FIXED_VIEWPORT_EQUIPMENT_TAB.childId, 4),
    PRAYER(WidgetInfo.FIXED_VIEWPORT_PRAYER_TAB.childId, 5),
    MAGIC(WidgetInfo.FIXED_VIEWPORT_MAGIC_TAB.childId, 6),
    FRIENDS(WidgetInfo.FIXED_VIEWPORT_FRIENDS_TAB.childId, 9),
    ACCOUNT(WidgetInfo.FIXED_VIEWPORT_IGNORES_TAB.childId, 8),
    LOGOUT(WidgetInfo.FIXED_VIEWPORT_LOGOUT_TAB.childId, 10),
    SETTINGS(WidgetInfo.FIXED_VIEWPORT_OPTIONS_TAB.childId, 11),
}

/*
    val hotkeyBit = mapOf(//signifies what tthe hotkey is set to rn
        Tab.COMBAT to 4675,
        )
 */