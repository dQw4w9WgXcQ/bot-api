package github.dqw4w9wgxcq.botapi.sceneentities.tile.items

import github.dqw4w9wgxcq.botapi.sceneentities.tile.TileEntities
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.item.TileItem
import net.runelite.api.Tile

object TileItems : TileEntities<TileItem>() {
    override fun extractFromUnsafe(tile: Tile): List<TileItem> {
        val groundItems = tile.groundItems ?: return emptyList()
        return groundItems.map { TileItem(it, tile) }
    }
}
