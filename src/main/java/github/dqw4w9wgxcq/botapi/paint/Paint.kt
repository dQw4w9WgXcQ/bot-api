package github.dqw4w9wgxcq.botapi.paint

import github.dqw4w9wgxcq.botapi.loader.RuneliteContext
import net.runelite.client.ui.overlay.Overlay
import net.runelite.client.ui.overlay.OverlayPriority
import java.awt.Dimension
import java.awt.Graphics2D

object Paint {
    private val overlays = mutableListOf<Overlay>()

    fun add(overlay: PaintOverlay) {
        RuneliteContext.getOverlayManager().add(overlay)
        synchronized(overlays) {
            overlays.add(overlay)
        }
    }

    fun remove(overlay: PaintOverlay) {
        synchronized(overlays) {
            overlays.remove(overlay)
        }

        RuneliteContext.getOverlayManager().remove(overlay)
    }

    fun clear() {
        synchronized(overlays) {
            overlays.clear()

            for (overlay in overlays) {
                RuneliteContext.getOverlayManager().remove(overlay)
            }
        }
    }

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
}
