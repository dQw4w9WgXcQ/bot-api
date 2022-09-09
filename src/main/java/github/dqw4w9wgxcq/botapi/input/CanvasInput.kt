package github.dqw4w9wgxcq.botapi.input

import github.dqw4w9wgxcq.botapi.Client
import java.awt.event.*

//do not change the parameters, they document properties the game checks
internal object CanvasInput {
    //mouselistener
    fun mouseClicked(isPopupTrigger: Boolean) {//never a popup trigger (on osx at least)
        val canvas = Client.canvas!!
        val mouseCanvasPosition = Client.mouseCanvasPosition
        val event = MouseEvent(
            canvas,
            MouseEvent.MOUSE_CLICKED,
            System.currentTimeMillis(),
            0,
            mouseCanvasPosition.x,
            mouseCanvasPosition.y,
            0,
            isPopupTrigger
        )
        canvas.mouseListeners.forEach { it.mouseClicked(event) }
    }

    //is a popup trigger when button is right click (3) (on osx at least)
    fun mousePressed(x: Int, y: Int, isPopupTrigger: Boolean, button: Int, isAltDown: Boolean, isMetaDown: Boolean) {
        val canvas = Client.canvas!!
        val buttonModifier = when (button) {//game never checks this modifier i think
            MouseEvent.BUTTON1 -> InputEvent.BUTTON1_DOWN_MASK
            MouseEvent.BUTTON2 -> InputEvent.BUTTON2_DOWN_MASK
            MouseEvent.BUTTON3 -> InputEvent.BUTTON3_DOWN_MASK
            else -> throw IllegalArgumentException("no mask for button: $button")
        }
        val modifiers =
            buttonModifier or (if (isAltDown) InputEvent.ALT_DOWN_MASK else 0) or if (isMetaDown) InputEvent.META_DOWN_MASK else 0
        val event = MouseEvent(
            canvas,
            MouseEvent.MOUSE_PRESSED,
            System.currentTimeMillis(),
            modifiers,
            x,
            y,
            0,
            isPopupTrigger,
            button
        )
        canvas.mouseListeners.forEach { it.mousePressed(event) }
    }

    fun mouseReleased(isPopupTrigger: Boolean) {//never a popup trigger (on osx at least)
        val canvas = Client.canvas!!
        val mouseCanvasPosition = Client.mouseCanvasPosition!!
        val event = MouseEvent(
            canvas,
            MouseEvent.MOUSE_RELEASED,
            System.currentTimeMillis(),
            0,
            mouseCanvasPosition.x,
            mouseCanvasPosition.y,
            0,
            isPopupTrigger
        )
        canvas.mouseListeners.forEach { it.mouseReleased(event) }
    }

    fun mouseEntered(x: Int, y: Int, `when`: Long) {// in game this just calles mouseMoved listener
        val canvas = Client.canvas!!
        val event = MouseEvent(canvas, MouseEvent.MOUSE_ENTERED, `when`, 0, x, y, 0, false)
        canvas.mouseListeners.forEach { it.mouseEntered(event) }
    }

    fun mouseExited(`when`: Long) {
        val canvas = Client.canvas!!
        val mouseCanvasPosition = Client.mouseCanvasPosition!!
        val event = MouseEvent(
            canvas,
            MouseEvent.MOUSE_EXITED,
            `when`,
            0,
            mouseCanvasPosition.x,
            mouseCanvasPosition.y,
            0,
            false
        )
        canvas.mouseListeners.forEach { it.mouseExited(event) }
    }

    //mousemotionlistener
    fun mouseDragged(x: Int, y: Int, `when`: Long) {//from mouse dragged listener, mouseMoved is called in game
        val canvas = Client.canvas!!
        val event = MouseEvent(canvas, MouseEvent.MOUSE_DRAGGED, `when`, 0, x, y, 0, false)
        canvas.mouseMotionListeners.forEach { it.mouseDragged(event) }
    }

    fun mouseMoved(x: Int, y: Int, `when`: Long) {
        val canvas = Client.canvas!!
        val event = MouseEvent(canvas, MouseEvent.MOUSE_MOVED, `when`, 0, x, y, 0, false)
        canvas.mouseMotionListeners.forEach { it.mouseMoved(event) }
    }

    //mousewheellistener
    fun mouseWheel(rotations: Int) {//up means down//rotations is always 1 or -1
        require(rotations == 1 || rotations == -1) { "rotations$rotations must be 1 or -1" }
        val canvas = Client.canvas!!
        val mouseCanvasPosition = Client.mouseCanvasPosition!!
        val event = MouseWheelEvent(
            canvas,
            MouseEvent.MOUSE_WHEEL,
            System.currentTimeMillis(),
            0,
            mouseCanvasPosition.x,
            mouseCanvasPosition.y,
            0,
            false,
            MouseWheelEvent.WHEEL_UNIT_SCROLL,
            rotations,
            rotations
        )
        canvas.mouseWheelListeners.forEach { it.mouseWheelMoved(event) }
    }

    //FocusListener
    fun focusGained() {
        val canvas = Client.canvas!!
        val event = FocusEvent(canvas, FocusEvent.FOCUS_GAINED, false, null)
        canvas.focusListeners.forEach { it.focusGained(event) }
    }

    fun focusLost() {
        val canvas = Client.canvas!!
        val event = FocusEvent(canvas, FocusEvent.FOCUS_LOST, true, null)
        canvas.focusListeners.forEach { it.focusLost(event) }
    }

    //keyboardlistener
    fun keyPressed(keyCode: Int, modifiers: Int) {
        val canvas = Client.canvas!!

        val event = KeyEvent(
            canvas,
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            modifiers,
            keyCode,
            KeyEvent.getExtendedKeyCodeForChar(keyCode).toChar(),
            KeyEvent.KEY_LOCATION_STANDARD
        )

        canvas.keyListeners.forEach { it.keyPressed(event) }
    }

    fun keyTyped(keyChar: Char) {//not sent on alt/ctrl/meta/shift/etc
        require(keyChar.code != KeyEvent.VK_UNDEFINED)
        val canvas = Client.canvas!!

        val event = KeyEvent(
            canvas,
            KeyEvent.KEY_TYPED,
            System.currentTimeMillis(),
            0,
            0,// always 0
            keyChar,
            KeyEvent.KEY_LOCATION_UNKNOWN
        )

        canvas.keyListeners.forEach { it.keyTyped(event) }
    }

    fun keyReleased(keyCode: Int) {
        val canvas = Client.canvas!!

        val event = KeyEvent(
            canvas,
            KeyEvent.KEY_RELEASED,
            System.currentTimeMillis(),
            0,
            keyCode,
            keyCode.toChar(),
            KeyEvent.KEY_LOCATION_STANDARD
        )

        canvas.keyListeners.forEach { it.keyReleased(event) }
    }
}
