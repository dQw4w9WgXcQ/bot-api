package github.dqw4w9wgxcq.botapi

import github.dqw4w9wgxcq.botapi.commons.info
import github.dqw4w9wgxcq.botapi.commons.warn
import github.dqw4w9wgxcq.botapi.loader.RuneliteContext

object Events {
    private val subscribers: MutableSet<Any> = HashSet()

    fun register(subscriber: Any) {
        info { "registering" + subscriber.javaClass.simpleName }
        subscribers.add(subscriber)
        RuneliteContext.getEventBus().register(subscriber)
    }

    fun unregister(subscriber: Any) {
        info { "unregistering " + subscriber.javaClass.simpleName }
        subscribers.remove(subscriber)
        RuneliteContext.getEventBus().unregister(subscriber)
    }

    fun clear() {
        val copy: List<Any> = subscribers.toList()//avoid concurrent modification
        for (listener in copy) {
            try {
                unregister(listener)
            } catch (e: Exception) {
                warn(e) { "exception unregistering $listener" }
            }
        }
    }
}