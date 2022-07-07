package github.dqw4w9wgxcq.botapi.input.mouse.action

import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.input.mouse.MouseInput
import java.awt.Rectangle

class ClickMouseAction(
    isPriority: Boolean,
    private val destination: Rectangle,
    private val left: Boolean,
    private val boundary: Rectangle?,
) : MouseAction(isPriority) {
    init {
        debug { "new click action isPriority=$isPriority, destination=$destination, boundary=$boundary, left=$left" }
    }

    override fun doAction(): Boolean {
        return MouseInput.internalClick(::interrupted, destination, left, boundary)
    }
}
