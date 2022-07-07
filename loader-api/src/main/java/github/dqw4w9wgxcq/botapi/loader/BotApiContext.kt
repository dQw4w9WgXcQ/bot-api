package github.dqw4w9wgxcq.botapi.loader

import net.runelite.api.Client
import net.runelite.client.callback.ClientThread
import net.runelite.client.eventbus.EventBus
import java.io.File

object BotApiContext {
    val dir = File(System.getProperty("user.home"), "runelite-bot")

    private var _client: Client? = null
    val client: Client
        get() {
            return _client ?: throw Exception("Client hasn't been set")
        }

    private var _clientThread: ClientThread? = null
    val clientThread: ClientThread
        get() {
            return _clientThread ?: throw Exception("ClientThread hasn't been set")
        }

    private var _eventBus: EventBus? = null
    val eventBus: EventBus
        get() {
            return _eventBus ?: throw Exception("EventBus not set yet")
        }

    //must be called before loading scripts
    fun initialize(client: Client, clientThread: ClientThread, eventBus: EventBus) {
        _client = client
        _clientThread = clientThread
        _eventBus = eventBus
    }
}