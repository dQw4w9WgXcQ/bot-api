package github.dqw4w9wgxcq.botapi.tabs

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.commons.info
import github.dqw4w9wgxcq.botapi.commons.waitUntil
import github.dqw4w9wgxcq.botapi.grandexchange.GrandExchange
import github.dqw4w9wgxcq.botapi.input.Keyboard
import github.dqw4w9wgxcq.botapi.itemcontainer.Bank
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

            if (Client.isResized) {
                throw IllegalStateException("not implemented open logout tab on resizable")
            }

            val logoutTab = Widgets.get(WidgetInfo.FIXED_VIEWPORT_LOGOUT_TAB)

            logoutTab.interact("logout")
        } else {
            Keyboard.press(hotkeyFor(tab))
        }

        waitUntil { isOpen(tab) }
    }

    fun isOpen(tab: Tab): Boolean {
        return Client.getVarcIntValue(VarClientInt.INVENTORY_TAB) == tab.varcInt
    }

    val logoutWq = WidgetQuery(WidgetID.LOGOUT_PANEL_ID) { it.hasAction("logout") }
    val worldSwitcherLogoutWq = WidgetQuery(WidgetID.WORLD_SWITCHER_GROUP_ID) { it.hasAction("logout") }
    fun logout(checkCloseInterfaces: Boolean = true) {
        if (Client.isResized) {
            info { "logging out resizable" }
            waitUntil(6 * 60_000) { Client.gameState == GameState.LOGIN_SCREEN }
            return
        }

        if (checkCloseInterfaces) {
            Bank.close()
            GrandExchange.close()
        }

        open(Tab.LOGOUT)

        var logoutButton = logoutWq.getOrNull()
        if (logoutButton == null) {
            logoutButton = worldSwitcherLogoutWq()
        }

        logoutButton.interact("logout")

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
