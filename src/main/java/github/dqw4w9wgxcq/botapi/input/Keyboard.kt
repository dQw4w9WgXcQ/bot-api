package github.dqw4w9wgxcq.botapi.input

import github.dqw4w9wgxcq.botapi.commons.Rand
import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.commons.info
import github.dqw4w9wgxcq.botapi.commons.wait
import java.awt.event.KeyEvent
import java.util.concurrent.Executors
import java.util.concurrent.Future

object Keyboard {
    private var threadCount = 0
    val exe = Executors.newCachedThreadPool {
        info { "new keyboard thread $threadCount" }
        Thread(it, "keyboard-${threadCount++}")
    }

    fun type(text: CharSequence, sendEnter: Boolean = false, lazyDelay: () -> Int = { Rand.nextInt(50..200) }) {
        for (c in text.toString().toCharArray()) {
            type(c)
            wait(lazyDelay())
        }

        if (sendEnter) {
            enter()
        }
    }

    fun typeKey(charCode: Int, modifiers: Int = 0, lazyDelay: () -> Int = { Rand.nextInt(50..200) }): Future<Unit> {
        val exKeyCode = KeyEvent.getExtendedKeyCodeForChar(charCode)
        require(exKeyCode != KeyEvent.VK_UNDEFINED)

        @Suppress("UNCHECKED_CAST")
        return exe.submit {
            Focus.require()
            debug {
                "charCode:$charCode modifiers:$modifiers charCode.toChar:${charCode.toChar()} exKeyCode:$exKeyCode exKeyCode.toChar:${exKeyCode.toChar()} exKeyCode lowercase:${
                    exKeyCode.toChar().lowercaseChar()
                }"
            }
            CanvasInput.keyPressed(exKeyCode, modifiers)
            wait(1, 3)
            CanvasInput.keyTyped(charCode.toChar())
            wait(lazyDelay())
            CanvasInput.keyReleased(exKeyCode)
        } as Future<Unit>
    }

    fun press(code: Int, modifiers: Int = 0, delay: () -> Int = { Rand.nextInt(50, 200) }): Future<Unit> {
        @Suppress("UNCHECKED_CAST")
        return exe.submit {
            Focus.require()
            CanvasInput.keyPressed(code, modifiers)
            wait(delay())
            CanvasInput.keyReleased(code)
        } as Future<Unit>
    }

    fun type(c: Char): Future<Unit> {
        debug { "char: $c code ${c.code}" }
        return typeKey(c.code)
    }

    fun enter(): Future<Unit> {
        return typeKey(KeyEvent.VK_ENTER)
    }

    fun space(): Future<Unit> {
        return typeKey(KeyEvent.VK_SPACE)
    }

    fun esc(): Future<Unit> {
        return typeKey(KeyEvent.VK_ESCAPE)
    }

    fun backspace(reps: Int = 1) {
        require(reps >= 1)
        var text = ""
        for (i in 0 until reps) {
            text += KeyEvent.VK_BACK_SPACE.toChar()
        }
        type(text)
    }
}
