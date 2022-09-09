package github.dqw4w9wgxcq.botapi.script.blockingevents

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.waitUntil
import github.dqw4w9wgxcq.botapi.interact.Interact
import github.dqw4w9wgxcq.botapi.itemcontainer.Inventory

class DeselectEvent : BlockingEvent() {
    override fun checkBlocked(): Boolean {
        if (Inventory.isItemSelected() || Client.spellSelected) {
            Interact.cancel()
            waitUntil { !Inventory.isItemSelected() && !Client.spellSelected }
        }

        return false
    }
}
