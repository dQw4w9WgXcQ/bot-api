package github.dqw4w9wgxcq.botapi.paint

import net.runelite.client.ui.overlay.Overlay
import net.runelite.client.ui.overlay.OverlayLayer
import net.runelite.client.ui.overlay.OverlayPosition
import net.runelite.client.ui.overlay.OverlayPriority
import java.awt.Dimension
import java.awt.Graphics2D

abstract class PaintOverlay : Overlay() {
    init {
        priority = OverlayPriority.HIGHEST
        position = OverlayPosition.DYNAMIC
        layer = OverlayLayer.ALWAYS_ON_TOP
    }

    abstract fun paint(g: Graphics2D)

    override fun render(graphics: Graphics2D?): Dimension? {
        paint(graphics!!)
        return null
    }
}