package api.commons

import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.event.Events
import net.runelite.api.events.ChatMessage
import net.runelite.client.eventbus.Subscribe

class ChatMessageWaiter(val messageMatches: (ChatMessage) -> Boolean) : () -> Boolean {
    init {
        Events.register(this)
    }

    @Volatile
    private var seen = false

    override fun invoke(): Boolean {
        return seen
    }

    @Subscribe
    fun onChatMessage(e: ChatMessage) {
        if (seen) {
            warn { "already seen, but still getting events?" }
            Events.unregister(this)
            return
        }

        if (messageMatches(e)) {
            seen = true
            Events.unregister(this)
        }
    }
}