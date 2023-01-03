package github.dqw4w9wgxcq.botapi.script

import github.dqw4w9wgxcq.botapi.script.blockingevents.BlockingEvent
import github.dqw4w9wgxcq.botapi.script.blockingevents.DeselectEvent
import github.dqw4w9wgxcq.botapi.script.blockingevents.LoginEvent
import github.dqw4w9wgxcq.botapi.script.blockingevents.WelcomeEvent

object BlockingEvents {
    init {
        reset()
    }

    lateinit var handlers: MutableList<BlockingEvent>

    fun reset() {
        handlers = mutableListOf(
            LoginEvent(),
            WelcomeEvent(),
            DeselectEvent(),
        )
    }

    fun checkBlocked(): Boolean {
        for (event in handlers) {
            if (event.checkBlocked()) return true
        }

        return false
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : BlockingEvent> get(clazz: Class<T>): T {
        return (handlers.firstOrNull { it.javaClass == clazz }
            ?: throw IllegalArgumentException("no event matched")) as T
    }
}