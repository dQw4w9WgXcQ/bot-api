package github.dqw4w9wgxcq.botapi.entities

import github.dqw4w9wgxcq.botapi.wrappers.entity.tile.`object`.*
import net.runelite.api.Tile

object TileObjects : TileEntities<TileObject<out net.runelite.api.TileObject>>() {
    override fun extractFromUnsafe(tile: Tile): List<TileObject<*>> {
        val out = mutableListOf<TileObject<*>>()

        val decorativeObject = tile.decorativeObject
        if (decorativeObject != null) {
            out.add(DecorativeObject(decorativeObject))
        }

        val groundObject = tile.groundObject
        if (groundObject != null) {
            out.add(GroundObject(groundObject))
        }

        val wallObject = tile.wallObject
        if (wallObject != null) {
            out.add(WallObject(wallObject))
        }

        val gameObjects = tile.gameObjects
        if (gameObjects != null) {
            for (gameObject in gameObjects) {
                if (gameObject != null && gameObject.sceneMinLocation == tile.sceneLocation) {
                    out.add(GameObject(gameObject))
                }
            }
        }

        return out.filter { it.name != "null" }
    }
}
