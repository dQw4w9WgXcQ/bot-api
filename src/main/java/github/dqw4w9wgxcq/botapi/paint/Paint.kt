package github.dqw4w9wgxcq.botapi.paint

import github.dqw4w9wgxcq.botapi.loader.RuneliteContext
import net.runelite.client.ui.overlay.Overlay

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
}
