package github.dqw4w9wgxcq.botapi.paint

import net.runelite.client.ui.overlay.Overlay
import net.runelite.client.ui.overlay.OverlayPriority
import java.awt.Dimension
import java.awt.Graphics2D

abstract class PaintOverlay : Overlay() {
    init {
        priority = OverlayPriority.HIGHEST
    }

    abstract fun paint(g: Graphics2D)

    override fun render(graphics: Graphics2D?): Dimension? {
        paint(graphics!!)
        return null
    }
}