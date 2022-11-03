package github.dqw4w9wgxcq.botapi.entities

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.wrappers.entity.tile.TileEntity
import net.runelite.api.Point
import net.runelite.api.Tile
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint

abstract class TileEntities<T : TileEntity> : SceneEntities<T>() {
    protected abstract fun extractFromUnsafe(tile: Tile): List<T>

    fun at(world: WorldPoint, matches: (T) -> Boolean): List<T> {
        return at(world.toScene(), matches)
    }

    fun at(x: Int, y: Int, matches: (T) -> Boolean): List<T> {
        return at(WorldPoint(x, y, -1), matches)
    }

    fun at(scene: Point, matches: (T) -> Boolean): List<T> {
        if (!scene.isInTrimmedScene()) {
            throw RetryException("scenePoint $scene not in trimmed scene")
        }

        return onGameThread {
            extractFromUnsafe(
                Client.scene.tiles[Client.plane][scene.x][scene.y]
                    ?: throw RetryException("Client.scene.tiles[Client.plane][scene.x][scene.y] is null")
            )
        }
            .filter(matches)
            .toList()
    }

    fun firstAtOrNull(worldPoint: WorldPoint, matches: (T) -> Boolean): T? {
        val localPoint = LocalPoint.fromWorld(Client, worldPoint.x, worldPoint.y)
            ?: throw RetryException("tryna get from a point $worldPoint outside the scene base: ${Client.baseX}, ${Client.baseY}")
        return firstAtOrNull(Point(localPoint.sceneX, localPoint.sceneY), matches)
    }

    fun firstAtOrNull(x: Int, y: Int, matches: (T) -> Boolean): T? {
        return firstAtOrNull(WorldPoint(x, y, -1), matches)
    }

    fun firstAtOrNull(x: Int, y: Int, id: Int): T? {
        return firstAtOrNull(x, y, byId(id))
    }

    fun firstAtOrNull(x: Int, y: Int, name: String): T? {
        return firstAtOrNull(x, y, byName(name))
    }

    fun firstAt(x: Int, y: Int, matches: (T) -> Boolean): T {
        return firstAtOrNull(x, y, matches) ?: throw NotFoundException("No tile entity found at $x, $y")
    }

    fun firstAt(x: Int, y: Int, id: Int): T {
        return firstAt(x, y, byId(id))
    }

    fun firstAt(x: Int, y: Int, name: String): T {
        return firstAt(x, y, byName(name))
    }

    fun firstAt(worldPoint: WorldPoint, matches: (T) -> Boolean): T {
        return firstAtOrNull(worldPoint, matches) ?: throw NotFoundException("No tile entity found at $worldPoint")
    }

    fun firstAtOrNull(scenePoint: Point, matches: (T) -> Boolean): T? {
        return at(scenePoint, matches).firstOrNull(matches)
    }

    fun firstAt(scenePoint: Point, matches: (T) -> Boolean): T {
        return firstAtOrNull(scenePoint, matches)
            ?: throw NotFoundException("No tile entity found at scene point $scenePoint")
    }

    override fun allUnsafe(matches: (T) -> Boolean): List<T> {
        return Client.scene
            .tiles[Client.plane]
            .copyOfRange(SCENE_FROM, SCENE_UNTIL)
            .flatMap { it.copyOfRange(SCENE_FROM, SCENE_UNTIL).asIterable() }
            .filterNotNull()
            .flatMap { extractFromUnsafe(it) }
            .filter(matches)
    }
}
