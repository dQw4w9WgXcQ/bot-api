package github.dqw4w9wgxcq.botapi.movement

import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.commons.isInTrimmedScene
import github.dqw4w9wgxcq.botapi.commons.toScene
import github.dqw4w9wgxcq.botapi.movement.pathfinding.local.LocalPathfinding
import github.dqw4w9wgxcq.botapi.sceneentities.Players
import net.runelite.api.Point
import net.runelite.api.coords.WorldPoint

class Path(val points: List<WorldPoint>) {
    constructor(vararg path: WorldPoint) : this(path.asList())

    private var currIndex: Int? = null

    fun reset() {
        currIndex = null
    }

    fun next(): WorldPoint? {
        if (currIndex == null) {
            val playerLoc = Players.local().sceneLocation

            var sawFirstWalkable = false

            for ((i, point) in points.withIndex()) {
                if (isWalkable(point, playerLoc)) {
                    if (!sawFirstWalkable) {
                        debug { "first walkable $point" }
                    }

                    sawFirstWalkable = true

                    currIndex = i
                } else if (sawFirstWalkable) {
                    debug { "first unwalkable $point" }
                    break
                }
            }

            if (currIndex == null) {
                debug { "no walkable" }
                return null
            }
        }

        val playerLoc = Players.local().sceneLocation

        while (currIndex!! < points.lastIndex && isWalkable(points[currIndex!! + 1], playerLoc)) {
            this.currIndex = currIndex!! + 1
            debug { "incremented currIndex $currIndex / ${points.lastIndex}" }
        }

        return points[currIndex!!]
    }

    private fun isWalkable(to: WorldPoint, from: Point): Boolean {
//        debug { to.isInScene(Client) }
//        debug { "world $to" }
//        debug { "baseX ${Client.baseX}" }
//        debug { "baseY ${Client.baseY}" }
//        debug { "scene ${to.toScene()}" }
//        debug { "from $from" }
        val scenePoint = to.toScene()
        return scenePoint.isInTrimmedScene() && LocalPathfinding.canReach(scenePoint, from, false)
    }
}
