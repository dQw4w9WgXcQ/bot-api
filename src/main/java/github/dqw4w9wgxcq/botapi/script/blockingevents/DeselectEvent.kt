package github.dqw4w9wgxcq.botapi.script.blockingevents

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.waitUntil
import github.dqw4w9wgxcq.botapi.interact.Interact

class DeselectEvent : BlockingEvent() {
    override fun checkBlocked(): Boolean {
        if (Client.isWidgetSelected) {
            Interact.cancel()
            waitUntil { !Client.isWidgetSelected }
        }

        return false
    }
}
