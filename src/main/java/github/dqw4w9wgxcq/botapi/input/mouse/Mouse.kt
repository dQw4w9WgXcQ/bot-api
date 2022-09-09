package github.dqw4w9wgxcq.botapi.input.mouse

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.RetryableBotException
import github.dqw4w9wgxcq.botapi.commons.toAwt
import github.dqw4w9wgxcq.botapi.commons.wait
import github.dqw4w9wgxcq.botapi.commons.waitUntil
import github.dqw4w9wgxcq.botapi.input.CanvasInput
import github.dqw4w9wgxcq.botapi.input.mouse.action.ClickMouseAction
import github.dqw4w9wgxcq.botapi.input.mouse.action.MoveMouseAction
import java.awt.Point
import java.awt.Rectangle
import java.util.concurrent.Future

object Mouse {
    val actionManager = MouseActionManager()

    @Volatile
    var lastPath: List<Point>? = null//for paint

    fun move(
        destination: Rectangle,
        boundary: Rectangle? = null,//used for when menu is open
    ) {
        actionManager.submit(MoveMouseAction(true, destination, boundary)).get()
    }

    fun click(
        destination: Rectangle,
        left: Boolean = true,
        boundary: Rectangle? = null,
    ) {
        actionManager.submit(ClickMouseAction(true, destination, left, boundary)).get()
    }

    fun asyncMove(destination: Rectangle, boundary: Rectangle? = null): Future<*> {
        return actionManager.submit(MoveMouseAction(false, destination, boundary))
    }

    fun scroll(rotations: Int, up: Boolean) {
        for (i: Int in 0..rotations) {
            CanvasInput.mouseWheel(if (up) -1 else 1)
        }
    }

    fun scrollUntil(boundary: Rectangle, up: Boolean, maxRotations: Int, condition: () -> Boolean) {
        asyncMove(boundary)
        waitUntil { boundary.contains(Client.mouseCanvasPosition.toAwt()) }

        for (i: Int in 0..maxRotations) {
            if (condition()) {
                return
            }

            scroll(1, up)
            wait(1..200)
        }

        throw RetryableBotException("after maxRotations $maxRotations condition $condition not met")
    }
}
