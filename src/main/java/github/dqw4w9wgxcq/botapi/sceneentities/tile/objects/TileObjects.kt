package github.dqw4w9wgxcq.botapi.sceneentities.tile.objects

import github.dqw4w9wgxcq.botapi.sceneentities.tile.TileEntities
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.`object`.DecorativeObject
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.`object`.GameObject
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.`object`.GroundObject
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.`object`.TileObject
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.`object`.WallObject
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
