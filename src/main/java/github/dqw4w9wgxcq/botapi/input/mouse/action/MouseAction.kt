package github.dqw4w9wgxcq.botapi.input.mouse.action

import github.dqw4w9wgxcq.botapi.commons.*

abstract class MouseAction(val isPriority: Boolean) : () -> Boolean {
    @Volatile
    var interrupted = false

    @Volatile
    var finished = false
        protected set

    abstract fun doAction(): Boolean

    override fun invoke(): Boolean {
        val result = if (interrupted) {
            debug { "$this is interrupted before start" }
            false
        } else {
            doAction()
        }

        finished = true

        return result
    }

    fun interrupt() {
        if (!isPriority) {
            interrupted = true
        }
    }
}
