package github.dqw4w9wgxcq.botapi.antiban

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.commons.inGameBounds
import github.dqw4w9wgxcq.botapi.commons.toAwt
import github.dqw4w9wgxcq.botapi.commons.wait
import github.dqw4w9wgxcq.botapi.input.Focus
import github.dqw4w9wgxcq.botapi.input.mouse.Mouse
import net.runelite.api.GameState
import java.awt.Rectangle
import kotlin.random.Random

object Antiban {
    @Volatile
    var active = false

    //todo call this somewhere
    fun initialize() {
        antibanThread.start()
    }

    private val antibanThread = Thread(
        {
            while (true) {
                if (active && Client.gameState == GameState.LOGGED_IN) {
                    when (Random.nextInt(0, 100)) {
//                        in 0 until 5 -> {
//                            camera()
//                        }

                        in 10 until 20 -> {
                            mouseMove()
                        }

                        in 40 until 60 -> {
                            loseFocus()
                        }
                    }
                }

                wait(0, 2000)
            }
        },
        "antiban"
    )

    var mouseMoveArea: Rectangle? = null//not the same as boundary.  need to bound the endpoint not the path

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
            debug { "skipping camera because mouse not in game" }
            return
        }

        debug { "camera" }
        TODO()
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
