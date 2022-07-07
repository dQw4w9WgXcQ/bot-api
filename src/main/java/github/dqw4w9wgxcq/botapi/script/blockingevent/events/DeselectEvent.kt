package github.dqw4w9wgxcq.botapi.script.blockingevent.events

import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.game.Client
import github.dqw4w9wgxcq.botapi.interact.Interact
import github.dqw4w9wgxcq.botapi.itemcontainer.inventory.Inventory
import github.dqw4w9wgxcq.botapi.script.blockingevent.BlockingEvent

class DeselectEvent : BlockingEvent() {
    override fun checkBlocked(): Boolean {
        if (Inventory.isItemSelected() || Client.spellSelected) {
            Interact.cancel()
            waitUntil { !Inventory.isItemSelected() && !Client.spellSelected }
        }

        return false
    }
}
