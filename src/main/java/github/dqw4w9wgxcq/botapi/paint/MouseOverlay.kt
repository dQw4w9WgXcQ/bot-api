package github.dqw4w9wgxcq.botapi.paint

import github.dqw4w9wgxcq.botapi.Client
import java.awt.Color
import java.awt.Graphics2D

object MouseOverlay : PaintOverlay() {
    override fun paint(g: Graphics2D) {
        val mouseCanvasPosition = Client.mouseCanvasPosition
        val x = mouseCanvasPosition.x
        val y = mouseCanvasPosition.y
        g.color = Color.MAGENTA
        g.drawOval(x - 3, y - 3, 5, 5)
    }
}