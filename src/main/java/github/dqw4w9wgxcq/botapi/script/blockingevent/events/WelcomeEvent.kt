package github.dqw4w9wgxcq.botapi.script.blockingevent.events

import github.dqw4w9wgxcq.botapi.commons.byAction
import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.commons.info
import github.dqw4w9wgxcq.botapi.commons.waitUntil
import github.dqw4w9wgxcq.botapi.game.Client
import github.dqw4w9wgxcq.botapi.script.blockingevent.BlockingEvent
import github.dqw4w9wgxcq.botapi.skill.Skills
import github.dqw4w9wgxcq.botapi.widget.WidgetQuery
import github.dqw4w9wgxcq.botapi.widget.Widgets
import net.runelite.api.Skill
import net.runelite.api.widgets.WidgetID
import net.runelite.api.widgets.WidgetInfo

//days since last logged in is 63
open class WelcomeEvent : BlockingEvent() {
    companion object {
        var doWhenOpen: (() -> Unit?)? = null

        val playButtonQuery = WidgetQuery(WidgetID.LOGIN_CLICK_TO_PLAY_GROUP_ID, byAction("play"))

        fun isOpen(): Boolean {
            return Widgets.getOrNull(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN) != null
        }
    }

    override fun checkBlocked(): Boolean {
        if (!isOpen() && !Client.isLoading) {
            return false
        }

        waitUntil { Skills.level(Skill.HITPOINTS) >= 10 }

        if (doWhenOpen != null) {
            debug { "doing doWhenOpen" }
            doWhenOpen!!()
        }

        if (Widgets.escClosesInterface()) {
            info { "closing welcome screen with esc" }
            Widgets.closeWithEsc()
        } else {
            info { "interacting play" }
            playButtonQuery().interact("play")
        }

        waitUntil { !isOpen() && !Client.isLoading }
        return false
    }
}
