package github.dqw4w9wgxcq.botapi.tabs.combat

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.waitUntil
import github.dqw4w9wgxcq.botapi.tabs.Tab
import github.dqw4w9wgxcq.botapi.tabs.Tabs
import github.dqw4w9wgxcq.botapi.widget.Widgets
import net.runelite.api.VarPlayer
import net.runelite.api.widgets.WidgetInfo

object Combat {
    val styleMap = listOf(
        WidgetInfo.COMBAT_STYLE_ONE,
        WidgetInfo.COMBAT_STYLE_TWO,
        WidgetInfo.COMBAT_STYLE_THREE,
        WidgetInfo.COMBAT_STYLE_FOUR,
    )

    fun selectStyle(index: Int) {
        if (Client.getVarpValue(VarPlayer.ATTACK_STYLE) != index) {
            Tabs.open(Tab.COMBAT)
            Widgets.get(styleMap[index]).interact { true }
        }
    }

    fun isAutoRetaliateEnabled(): Boolean {
        return Client.getVarpValue(172) == 0
    }

    fun checkAutoRetaliate(enabled: Boolean) {
        if (isAutoRetaliateEnabled() == enabled) {
            return
        }

        Tabs.open(Tab.COMBAT)
        Widgets.get(WidgetInfo.COMBAT_AUTO_RETALIATE).interact("Auto retaliate")
        waitUntil { isAutoRetaliateEnabled() == enabled }
    }
}
