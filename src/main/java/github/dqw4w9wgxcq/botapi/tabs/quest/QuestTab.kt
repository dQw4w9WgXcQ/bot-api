package github.dqw4w9wgxcq.botapi.tabs.quest

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.byAction
import github.dqw4w9wgxcq.botapi.commons.waitUntil
import github.dqw4w9wgxcq.botapi.entities.Players
import github.dqw4w9wgxcq.botapi.widget.WidgetQuery
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.WidgetInfo

object QuestTab {
    fun setMinigame(index: Int) {
        Client.runScript(124, index)
    }

    fun teleMinigame(index: Int) {
        setMinigame(index)
        //idk if need sleep here
        val teleInfo = WidgetInfo.MINIGAME_TELEPORT_BUTTON
        WidgetQuery(teleInfo.groupId, teleInfo.childId, byAction("teleport to")).invoke().interact("teleport to")
        waitUntil(5000) { Players.local().animation == 4847 }
        val startLoc: WorldPoint = Players.local().worldLocation
        waitUntil(30_000) { Players.local().worldLocation != startLoc }
    }
}
