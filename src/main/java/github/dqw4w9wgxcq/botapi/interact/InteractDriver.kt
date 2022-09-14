package github.dqw4w9wgxcq.botapi.interact

import github.dqw4w9wgxcq.botapi.wrappers.Widget
import github.dqw4w9wgxcq.botapi.wrappers.entity.Entity
import github.dqw4w9wgxcq.botapi.wrappers.item.container.InventoryItem
import net.runelite.api.Point

interface InteractDriver {
    fun cancel()
    fun walk(scenePosition: Point)
    fun withInventory(invItem: InventoryItem, actionMatches: (String) -> Boolean)
    fun withEntity(target: Entity, actionMatches: (String) -> Boolean)
    fun withWidget(target: Widget, actionMatches: (String) -> Boolean)
}