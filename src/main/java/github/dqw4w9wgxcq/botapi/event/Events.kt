package github.dqw4w9wgxcq.botapi.event

import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.loader.BotApiContext

object Events {
    private val subscribers: MutableSet<Any> = HashSet()

    fun register(subscriber: Any) {
        info { "registering" + subscriber.javaClass.simpleName }
        subscribers.add(subscriber)
        BotApiContext.getEventBus().register(subscriber)
    }

    fun unregister(subscriber: Any) {
        info { "unregistering " + subscriber.javaClass.simpleName }
        subscribers.remove(subscriber)
        BotApiContext.getEventBus().unregister(subscriber)
    }

    fun clear() {
        val copy: List<Any> = subscribers.toList()//avoid concurrent modification
        for (listener in copy) {
            try {
                unregister(listener)
            } catch (e: Exception) {
                warn(e) { "some exception when unregistering class: ${listener.javaClass.simpleName}" }
            }
        }
    }
}