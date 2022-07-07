package github.dqw4w9wgxcq.botapi.tabs

import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.game.Client
import github.dqw4w9wgxcq.botapi.input.Keyboard
import github.dqw4w9wgxcq.botapi.itemcontainer.bank.Bank
import github.dqw4w9wgxcq.botapi.widget.WidgetQuery
import github.dqw4w9wgxcq.botapi.widget.Widgets
import net.runelite.api.GameState
import net.runelite.api.VarClientInt
import net.runelite.api.widgets.WidgetID
import net.runelite.api.widgets.WidgetInfo
import java.awt.event.KeyEvent

object Tabs {
    fun open(tab: Tab) {
        if (isOpen(tab)) {
            return
        }

        debug { "opening tab $tab" }
        if (tab == Tab.LOGOUT) {
            debug { "logout must be opened with mouse" }

            val logoutTab = let {
                val classicResizableTab = Widgets.getOrNull(WidgetInfo.RESIZABLE_VIEWPORT_LOGOUT_TAB)
                if (classicResizableTab == null || classicResizableTab.isHidden) {
                    Widgets.get(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_LOGOUT_BUTTON)
                } else {
                    classicResizableTab
                }
            }

            logoutTab.interact("logout")
        } else {
            Keyboard.press(hotkeyFor(tab))
        }

        waitUntil { isOpen(tab) }
    }

    fun isOpen(tab: Tab): Boolean {
        return Client.getVarcIntValue(VarClientInt.INVENTORY_TAB) == tab.varcInt
    }

    fun logout() {
        Bank.close()

        open(Tab.LOGOUT)

        (WidgetQuery(WidgetID.LOGOUT_PANEL_ID) { it.hasAction("logout") }.getOrNull()
            ?: Widgets.get(WidgetID.WORLD_SWITCHER_GROUP_ID, 23))
            .interact("logout")

        waitUntil { Client.gameState == GameState.LOGIN_SCREEN }
    }

    private fun hotkeyFor(tab: Tab): Int = when (tab) {
        Tab.COMBAT -> KeyEvent.VK_F1
        Tab.SKILLS -> KeyEvent.VK_F2
        Tab.QUESTS -> KeyEvent.VK_F3
        Tab.INVENTORY -> KeyEvent.VK_ESCAPE
        Tab.EQUIPMENT -> KeyEvent.VK_F4
        Tab.PRAYER -> KeyEvent.VK_F5
        Tab.MAGIC -> KeyEvent.VK_F6
        Tab.SETTINGS -> KeyEvent.VK_F10
        else -> throw IllegalArgumentException("no hotkey for tab $tab")
    }
}
