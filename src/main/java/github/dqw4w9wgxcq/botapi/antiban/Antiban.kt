package github.dqw4w9wgxcq.botapi.antiban

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.input.Focus
import github.dqw4w9wgxcq.botapi.input.Keyboard
import github.dqw4w9wgxcq.botapi.input.mouse.Mouse
import net.runelite.api.GameState
import java.awt.Rectangle
import java.awt.event.KeyEvent
import kotlin.random.Random

object Antiban {
    var mouseMoveArea: Rectangle? = null//not the same as boundary.  need to bound the endpoint not the path

    fun maybeDoAntiban() {
        if (Client.gameState == GameState.LOGGED_IN) {
            wait(500)
            when (Random.nextInt(0, 100)) {
                in 0 until 20 -> {
                    camera()
                }

//                        in 10 until 20 -> {
//                            mouseMove()
//                        }

                in 40 until 60 -> {
                    loseFocus()
                }
            }
        }
    }

    fun mouseMove() {
        val mousePos = Client.mouseCanvasPosition.toAwt()
        if (!inGameBounds(mousePos)) {
            debug { "skipping mouseMove because mouse not in game" }
            return
        }

        when (Random.nextInt(0, 100)) {
            in 70..89 -> {
                debug { "200" }
                Mouse.asyncMove(Rectangle(mousePos.x - 200, mousePos.y - 200, mousePos.x + 200, mousePos.y + 200))
            }

            in 90..99 -> {
                debug { "1000" }
                Mouse.asyncMove(Rectangle(mousePos.x - 1000, mousePos.y - 1000, mousePos.x + 1000, mousePos.y + 1000))
            }

            else -> {
                val radius = Random.nextInt(1, 300)
                Mouse.asyncMove(Rectangle(mousePos.x - radius, mousePos.y - radius, radius * 2, radius * 2))
            }
        }
    }

    fun camera() {
        if (!inGameBounds(Client.mouseCanvasPosition.toAwt())) {
            info { "skipping camera because mouse not in game" }
            return
        }

        if (!Client.hasFocus()) {
            info { "skipping camera because client not focuesd" }
            return
        }

        Focus.require(2000)

        info { "camera" }

        val arrowKeys = listOf(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_UP)
        Keyboard.press(
            arrowKeys.random(),
            delay = { Random.nextInt(50, 1000) })
    }

    fun loseFocus() {
        if (inGameBounds(Client.mouseCanvasPosition.toAwt())) {
            debug { "skipping lose focus because mouse in game" }
            return
        }

        debug { "lose focus" }
        Focus.lose()
    }
}
