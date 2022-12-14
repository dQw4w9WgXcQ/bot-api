package github.dqw4w9wgxcq.botapi.paint

import github.dqw4w9wgxcq.botapi.loader.RuneliteContext

object Paint {
    fun add(overlay: PaintOverlay) {
        RuneliteContext.getOverlayManager().add(overlay)
    }

    fun remove(overlay: PaintOverlay) {
        RuneliteContext.getOverlayManager().remove(overlay)
    }

    fun clear() {
        RuneliteContext.getOverlayManager().removeIf { it is PaintOverlay }
    }
}
