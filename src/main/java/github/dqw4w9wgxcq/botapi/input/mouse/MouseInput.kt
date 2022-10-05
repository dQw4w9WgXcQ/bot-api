package github.dqw4w9wgxcq.botapi.input.mouse

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.input.CanvasInput
import github.dqw4w9wgxcq.botapi.input.Focus
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent
import kotlin.math.hypot
import kotlin.random.Random

//internal mouse functions to be used in MouseAction
object MouseInput {
    private val paths = MousePathGenerator()

    fun internalClick(
        isInterrupted: () -> Boolean,
        destination: Rectangle,
        left: Boolean,
        boundary: Rectangle?
    ): Boolean {
        if (!destination.contains(Client.mouseCanvasPosition.toAwt())) {
            if (!internalMove(isInterrupted, destination.randomPoint(), false, boundary)) {
                return false
            }
        } else {
            wait(Rand.nextInt(500))
        }

        if (isInterrupted()) {
            debug { "interrupted before click" }
            return false
        }

        sendClickEvents(left)
        val mousePos = Client.mouseCanvasPosition.toAwt()
        Mouse.asyncMove(Rectangle(mousePos.x - 25, mousePos.y - 25, 50, 50))
        return true
    }

    fun internalMove(
        isInterrupted: () -> Boolean,
        endPoint: Point,
        drag: Boolean,
        boundary: Rectangle?
    ): Boolean {
        val mouseCanvasPosition = Client.mouseCanvasPosition.toAwt()
        val startPoint = if (inGameBounds(mouseCanvasPosition)) {
            mouseCanvasPosition
        } else {
            if (drag) {
                throw RetryableBotException("cant drag if not in game bounds")
            }

            val x: Int
            val y: Int

            if (lastExitTime + 3000 > System.currentTimeMillis()) {
                val lastExitPointFinal = lastExitPoint!!
                info { "last exit time wasn't longer than 3 seconds ago so need to enter from near lastExitPoint$lastExitPointFinal" }
                x = minOf(maxOf(lastExitPointFinal.x + Rand.nextInt(-25, 25), 0), Client.canvasWidth - 1)
                y = minOf(maxOf(lastExitPointFinal.y + Rand.nextInt(-25, 25), 0), Client.canvasHeight - 1)
            } else {
                if (Random.nextBoolean()) {
                    x = if (Random.nextBoolean()) 0 else Client.canvasWidth - 1
                    y = Rand.nextInt(Client.canvasHeight)
                } else {
                    x = Rand.nextInt(Client.canvasWidth)
                    y = if (Random.nextBoolean()) 0 else Client.canvasHeight - 1
                }
            }

            val enterPoint = Point(x, y)
            info { "entering from $enterPoint" }
            enterPoint
        }

        val distance = hypot(
            (endPoint.x - startPoint.x).toDouble(),
            (endPoint.y - startPoint.y).toDouble()
        ).toInt()//hypot is always positive
        val ticks = (distance / Rand.nextInt(75, 200)) + 5

        val path = paths.findPath(startPoint, endPoint, ticks, boundary)

        debug { "retrived path of len ${path.size}" }

        require(path.isNotEmpty()) { "path is empty start:$startPoint end:$endPoint" }

        for ((i, point) in path.withIndex()) {
            if (isInterrupted()) {
                debug { "interrupted at index:$i" }
                return false
            }
            wait(25)
            sendMoveEvent(point, drag)
        }

        return true
    }

    internal fun sendClickEvents(left: Boolean) {
        val mousePosition = Client.mouseCanvasPosition.toAwt()
        debug { "canvasClick ${mousePosition.x}, ${mousePosition.y}, left:$left" }
        if (!inGameBounds(mousePosition)) {
            debug { "mouse$mousePosition not in game bounds" }
            throw RetryableBotException("mouse$mousePosition not in game bounds, cant click")
        }

        val button = if (left) MouseEvent.BUTTON1 else MouseEvent.BUTTON3

        CanvasInput.mousePressed(
            mousePosition.x, mousePosition.y, !left, button, isAltDown = false, isMetaDown = false
        )

        if (left) {
            Focus.require()
        }

        wait(1, 150)

        CanvasInput.mouseReleased(false)
        CanvasInput.mouseClicked(false)
    }

    private lateinit var previousPoint: Point

    private var previousInBounds: Boolean = false

    private var lastExitTime: Long = Long.MIN_VALUE

    private var lastExitPoint: Point? = null

    private fun sendMoveEvent(to: Point, drag: Boolean) {
        if (!this::previousPoint.isInitialized) {
            previousPoint = Client.mouseCanvasPosition.toAwt()
            previousInBounds = inGameBounds(previousPoint)
        }
//        class PreviousPointMismatchException(val canvasPoint: Point, val point: Point) :
//            RuntimeException("mismatching canvasPoint $canvasPoint point $point")
//        val canvasPosition = Client.mouseCanvasPosition.toAwt()
//        if (this::previousPoint.isInitialized && previousPoint != canvasPosition) {
//            debug { "mismatch detected (we probably moved the mouse), resetting" }
//            previousPoint = canvasPosition
//            previousInBounds = inGameBounds(canvasPosition)
//            throw PreviousPointMismatchException(canvasPosition, to)
//        }

        val inBounds = inGameBounds(to)
        if (inBounds != previousInBounds) {
            if (inBounds) {
                //todo if the mouse is moving fast, x or y can be off by a bit
                CanvasInput.mouseEntered(to.x, to.y, System.currentTimeMillis())
            } else {
                CanvasInput.mouseExited(System.currentTimeMillis())
                previousInBounds = false
                previousPoint = Point(-1, -1)
                lastExitPoint = to
                lastExitTime = System.currentTimeMillis()
            }
        }

        if (!drag && !inBounds) {
            //require(previousPoint.x == -1 && previousPoint.y == -1)
            return
        }

        if (drag) {
            CanvasInput.mouseDragged(to.x, to.y, System.currentTimeMillis())
        } else {
            CanvasInput.mouseMoved(to.x, to.y, System.currentTimeMillis())
        }
        previousInBounds = inBounds
        previousPoint = to
    }
}