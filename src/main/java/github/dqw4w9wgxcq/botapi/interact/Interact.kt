package github.dqw4w9wgxcq.botapi.interact

import github.dqw4w9wgxcq.botapi.Client
import net.runelite.api.Constants
import net.runelite.api.MenuEntry
import java.awt.Rectangle

object Interact : InteractDriver by RickkInteract() {
    val viewportBounds
        get() = Rectangle(
            4,
            4,
            512 + (Client.canvasWidth - Constants.GAME_FIXED_WIDTH) - 1,
            334 + (Client.canvasHeight - Constants.GAME_FIXED_HEIGHT) - 1
        )

    fun MenuEntry.toString2() =
        "[option: $option target: $target identifier: $identifier type: $type param0: $param0 param1: $param1 isForcedLeftClick: $isForceLeftClick isDeprioritized : $isDeprioritized]"
}
