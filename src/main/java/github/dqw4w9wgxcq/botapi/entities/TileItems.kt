package github.dqw4w9wgxcq.botapi.entities

import github.dqw4w9wgxcq.botapi.wrappers.entity.tile.item.TileItem
import net.runelite.api.Tile

object TileItems : TileEntities<TileItem>() {
    override fun extractFromUnsafe(tile: Tile): List<TileItem> {
        val groundItems = tile.groundItems ?: return emptyList()
        return groundItems.map { TileItem(it, tile) }
    }
}
