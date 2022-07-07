package github.dqw4w9wgxcq.botapi.input.mouse.action

import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.input.mouse.MouseInput
import java.awt.Rectangle

class MoveMouseAction(
    isPriority: Boolean,
    private val destination: Rectangle,
    private val boundary: Rectangle?
) : MouseAction(isPriority) {
    override fun doAction(): Boolean {
        return MouseInput.internalMove(::interrupted, destination.randomPoint(), false, boundary)
    }
}
