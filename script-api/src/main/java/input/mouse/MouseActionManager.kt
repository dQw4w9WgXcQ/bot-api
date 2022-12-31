package github.dqw4w9wgxcq.botapi.input.mouse

import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.input.mouse.action.MouseAction
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

class MouseActionManager {
    private lateinit var inputThread: Thread

    private val exe = Executors.newSingleThreadExecutor {
        inputThread = Thread(it, "mouse")
        inputThread
    }

    private val pending = ArrayList<MouseAction>()//access must be synchronized

    fun submit(action: MouseAction): Future<Boolean> {
        debug { "Submitting action: $action" }
        synchronized(pending) {
            pending.removeIf { it.finished }

            if (action.isPriority) {
                pending.forEach { it.interrupt() }
                pending.removeIf { it.finished }
            }

            debug { "Pending actions: $pending" }
            pending.add(action)
            return exe.submit(Callable { action() })
        }
    }
}