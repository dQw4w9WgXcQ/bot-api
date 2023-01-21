package github.dqw4w9wgxcq.botapi.interact

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.Reflect
import github.dqw4w9wgxcq.botapi.Reflect.setBoolean2
import github.dqw4w9wgxcq.botapi.Reflect.setInt2
import github.dqw4w9wgxcq.botapi.commons.RetryException
import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.commons.onGameThread
import github.dqw4w9wgxcq.botapi.wrappers.Widget
import github.dqw4w9wgxcq.botapi.wrappers.entity.Entity
import github.dqw4w9wgxcq.botapi.wrappers.item.container.InventoryItem
import net.runelite.api.Point

class MenuInteract : InteractDriver {
    override fun cancel() {
        TODO("Not yet implemented")
    }

    override fun walk(scenePosition: Point) {
        debug { "walking $scenePosition" }
        val baseX = Client.baseX
        val baseY = Client.baseY
        onGameThread {
            val newBaseX = Client.baseX
            val newBaseY = Client.baseY
            if (baseX != newBaseX || baseY != newBaseY) {
                throw RetryException("base changed $baseX,$baseY to $newBaseX,$newBaseY")
            }
            Reflect.selectedX.setInt2(null, scenePosition.x, 1)
            Reflect.selectedY.setInt2(null, scenePosition.y, 1)
            Reflect.viewportWalking.setBoolean2(null, true)
        }
    }

    override fun withInventory(invItem: InventoryItem, actionMatches: (String) -> Boolean) {
        TODO("Not yet implemented")
    }

    override fun withEntity(target: Entity, actionMatches: (String) -> Boolean) {
        TODO("Not yet implemented")
    }

    override fun withWidget(target: Widget, actionMatches: (String) -> Boolean) {
        TODO("Not yet implemented")
    }
}