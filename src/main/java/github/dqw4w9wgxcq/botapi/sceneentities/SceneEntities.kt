package github.dqw4w9wgxcq.botapi.sceneentities

import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.sceneentities.actors.players.Players
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.SceneEntity
import kotlin.math.ceil

abstract class SceneEntities<out E : SceneEntity> {
    protected abstract fun allUnsafe(matches: (E) -> Boolean): List<E>

    fun all(matches: (E) -> Boolean = { true }): List<E> {
        return onGameThread { allUnsafe { matches(it) } }
    }

    fun closestOrNull(matches: (E) -> Boolean): E? {
        val fromLoc = Players.local().sceneLocation

        var closestDist = Integer.MAX_VALUE
        var closest: E? = null
        for (entity in all(matches)) {
            val dist = ceil(entity.sceneLocation.pythagDist(fromLoc)).toInt()
            if (closest == null || dist < closestDist) {
                closest = entity
                closestDist = dist
                continue
            }
        }

        return closest
    }

    fun closestOrNull(vararg ids: Int): E? {
        return closestOrNull(byId(*ids))
    }

    fun closestOrNull(vararg namesEqualIgnoreCase: String): E? {
        return closestOrNull(byName(*namesEqualIgnoreCase))
    }

    fun closest(matches: (E) -> Boolean): E {
        return closestOrNull(matches) ?: throw NotFoundException("No matching $matches found")
    }

    fun closest(vararg ids: Int): E {
        return closestOrNull(*ids) ?: throw NotFoundException("No matching found for ids ${ids.joinToString()}")
    }

    fun closest(vararg names: String): E {
        return closestOrNull(*names) ?: throw NotFoundException("No matching found for names ${names.joinToString()}")
    }
}
