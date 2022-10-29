package github.dqw4w9wgxcq.botapi.movement.pathfinding.local

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.entities.Players
import github.dqw4w9wgxcq.botapi.wrappers.Locatable
import net.runelite.api.CollisionDataFlag
import net.runelite.api.ObjectID
import net.runelite.api.Point
import net.runelite.api.WallObject
import net.runelite.api.coords.WorldPoint
import kotlin.properties.Delegates

object LocalPathfinding {
    enum class WallObstacle(//must match ((name and action) or id) and not blacklistids
        val names: Set<String>,
        val ids: Set<Int>,
        val index0Actions: Set<String>,
        val idDisallowList: Set<Int>
    ) {
        DOOR(
            setOf("Door", "Large door", "Gate", "Large gate", "Longhall door"),
            setOf(),
            setOf("Open"),
            setOf(
                ObjectID.GATE_44052, ObjectID.GATE_44053,//al kharid toll gate
                ObjectID.DOOR_136,//draynor manor exit door
                ObjectID.DOOR_24958//cooks guild door
            )
        ),
        WEB(
            setOf(),
            setOf(ObjectID.WEB),
            setOf("Slash"),
            setOf()
        ),
        ;

        fun test(wallObject: WallObject): Boolean {
            val id = wallObject.id

            if (idDisallowList.contains(id)) return false

            if (ids.contains(id)) return true

            val definition = Client.getObjectDefinition(id)
            if (names.contains(definition.name)) {
                val firstAction = definition.actions?.get(0) ?: return false
                return index0Actions.contains(firstAction)
            }

            return false
        }
    }

    private var _map: GridMap? = null
    val map: GridMap
        get() {
            if (_map == null || Client.collisionMaps!![Client.plane].flags.contentDeepHashCode() != _map!!.originalFlagsHash) {
                debug { "new map" }
                _map = GridMap()
            }

            return _map!!
        }

    class GridMap {
        var originalFlagsHash by Delegates.notNull<Int>()
        val flags = onGameThread {
            val flags = Client.collisionMaps!![Client.plane].flags!!
            originalFlagsHash = flags.contentDeepHashCode()
            val out = flags.map { it.clone() }.toTypedArray()//deep clone
            for ((x, column) in Client.scene.tiles[Client.plane].withIndex()) {
                if (column == null) continue
                for ((y, tile) in column.withIndex()) {
                    if (tile == null) continue
                    val wallObject = tile.wallObject ?: continue

                    if (WallObstacle.values().any { it.test(wallObject) }) {
                        removeObjectFlags(x, y, wallObject.config, out)
                    }
                }
            }
            out
        }

        val graph = Array(flags.size) { x -> Array(flags[0].size) { y -> GridVertex(x, y, null, flags) } }
        var zoneCount = 0

        //copy pasted from decommpiled game
        private fun removeObjectFlags(x: Int, y: Int, config: Int, flags: Array<IntArray>) {
            fun removeFlag(var1: Int, var2: Int, var3: Int) {
                val var10000 = flags[var1]
                var10000[var2] = var10000[var2] and var3.inv()
            }

            val xConfig: Int = config and 31
            val yConfig: Int = config shr 6 and 3
            if (xConfig == 0) {
                if (yConfig == 0) {
                    removeFlag(x, y, 128)
                    removeFlag(x - 1, y, 8)
                }
                if (yConfig == 1) {
                    removeFlag(x, y, 2)
                    removeFlag(x, y + 1, 32)
                }
                if (yConfig == 2) {
                    removeFlag(x, y, 8)
                    removeFlag(x + 1, y, 128)
                }
                if (yConfig == 3) {
                    removeFlag(x, y, 32)
                    removeFlag(x, y - 1, 2)
                }
            }
            if (xConfig == 1 || xConfig == 3) {
                if (yConfig == 0) {
                    removeFlag(x, y, 1)
                    removeFlag(x - 1, y + 1, 16)
                }
                if (yConfig == 1) {
                    removeFlag(x, y, 4)
                    removeFlag(x + 1, y + 1, 64)
                }
                if (yConfig == 2) {
                    removeFlag(x, y, 16)
                    removeFlag(x + 1, y - 1, 1)
                }
                if (yConfig == 3) {
                    removeFlag(x, y, 64)
                    removeFlag(x - 1, y - 1, 4)
                }
            }
            if (xConfig == 2) {
                if (yConfig == 0) {
                    removeFlag(x, y, 130)
                    removeFlag(x - 1, y, 8)
                    removeFlag(x, y + 1, 32)
                }
                if (yConfig == 1) {
                    removeFlag(x, y, 10)
                    removeFlag(x, y + 1, 32)
                    removeFlag(x + 1, y, 128)
                }
                if (yConfig == 2) {
                    removeFlag(x, y, 40)
                    removeFlag(x + 1, y, 128)
                    removeFlag(x, y - 1, 2)
                }
                if (yConfig == 3) {
                    removeFlag(x, y, 160)
                    removeFlag(x, y - 1, 2)
                    removeFlag(x - 1, y, 8)
                }
            }
        }
    }

    class GridVertex(val x: Int, val y: Int, var zone: Int?, flags: Array<IntArray>) {
        val adjacentEdges: List<GridEdge> by lazy {
            buildList {
                for (direction in 0..6 step 2) {
                    if (flags.canTravelInDirection(x, y, direction)) {
                        add(GridEdge(direction))
                    }
                }
            }
        }

        val edges: List<GridEdge> by lazy {
            adjacentEdges
        }

//        val diagonalEdges: List<GridEdge> by lazy {
//            buildList {
//                for (direction in 1..7 step 2) {
//                    if (canTravelInDirection(x, y, direction, flags, doors)) {
//                        add(GridEdge(direction))
//                    }
//                }
//            }
//        }
    }

    class GridEdge(val direction: Int)

    fun findPath(
        to: WorldPoint,
        from: WorldPoint,
        ignoreEndObject: Boolean,
    ): List<Point>? {
        return findPath(to.toScene(), from.toScene(), ignoreEndObject)
    }

    fun findPath(
        to: Locatable,
        from: Locatable,
        ignoreEndObject: Boolean,
    ): List<Point>? {
        return findPath(to.sceneLocation, from.sceneLocation, ignoreEndObject)
    }

    fun findPath(
        to: Point,
        from: Point,
        ignoreEndObject: Boolean,
    ): List<Point>? {
        val map = map//ensure the map doesn't change between reachable and finding path

        //if we are standing on a blocked tile(this is observed when just teleproted to fairy ring)
        if (map.flags[from.x][from.y] and CollisionDataFlag.BLOCK_MOVEMENT_FULL != 0) {
            info { "we are standing on a blocked tile" }
            val adjacentEdges = map.graph[from.x][from.y].adjacentEdges

            if (adjacentEdges.isEmpty()) {
                throw Exception("should never happen, we are on standing on blocked and all adjacent r blocked ${Players.local().worldLocation}")
            }

            return adjacentEdges.mapNotNull {
                val adjacentFrom = Point(from.x + dx(it.direction), from.y + dy(it.direction))
                if (!map.canReach(to, adjacentFrom, ignoreEndObject)) {
                    null
                } else {
                    findPathIgnoreReachable(to, adjacentFrom, ignoreEndObject, map)
                }
            }.minByOrNull { it.size }
        }

        if (!map.canReach(to, from, ignoreEndObject)) {
            return null
        }

        return findPathIgnoreReachable(to, from, ignoreEndObject, map)
    }

    internal fun findPathIgnoreReachable(to: Point, from: Point, ignoreEndObject: Boolean, map: GridMap): List<Point> {
        debug { "to $to from $from" }

        val frontier = mutableListOf(from)
        val seenFrom = mutableMapOf<Point, Point?>(from to null)

        val adjacentToEnd = buildSet {
            for (direction in 0..6 step 2) {
                if (map.flags.canTravelInDirection(to.x, to.y, direction)) {
                    add(Point(to.x + dx(direction), to.y + dy(direction)))
                }
            }
        }

        val startTime = System.currentTimeMillis()
        while (frontier.isNotEmpty()) {
            val curr = frontier.removeFirst()
            //debug { "curr $curr" }

            if (curr == to || (ignoreEndObject && adjacentToEnd.contains(curr) && map.flags[to.x][to.y] and CollisionDataFlag.BLOCK_MOVEMENT_FULL != 0)) {
                val foundPathTime = System.currentTimeMillis()
                debug { "found path in ${foundPathTime - startTime}ms" }
                val backtrackStartTime = System.currentTimeMillis()
                val backtrack = mutableListOf(curr)
                while (backtrack.last() != from) {
                    backtrack.add(seenFrom[backtrack.last()]!!)
                }
                val backtrackTime = System.currentTimeMillis()
                debug { "backtracked  ${backtrackTime - backtrackStartTime}ms" }
                return backtrack.reversed()
            }

            for (edge in map.graph[curr.x][curr.y].edges) {
                val next = Point(curr.x + dx(edge.direction), curr.y + dy(edge.direction))

                if (seenFrom.contains(next)) {
                    //debug { "already seen $next" }
                    continue
                }

                //debug { "adding to frontier $next" }
                frontier.add(next)
                seenFrom[next] = curr
            }
        }

        throw Exception("cant find path from $from to $to")
    }

    fun canReach(to: WorldPoint, ignoreEndObject: Boolean = true): Boolean {
        return map.canReach(to.toScene(), Players.local().sceneLocation, ignoreEndObject)
    }

    fun canReach(to: Locatable, ignoreEndObject: Boolean = true): Boolean {
        return map.canReach(to.sceneLocation, Players.local().sceneLocation, ignoreEndObject)
    }

    fun canReach(to: Point, from: Point, ignoreEndObject: Boolean = true): Boolean {
        return map.canReach(to, from, ignoreEndObject)
    }

    fun GridMap.canReach(to: Point, from: Point, ignoreEndObject: Boolean): Boolean {
        if (!to.isInTrimmedScene()) {
            throw RetryableBotException("to $to is not in trimmed scene")
        }

        if (!from.isInTrimmedScene()) {
            throw RetryableBotException("from $from not in trimmed scene")
        }

        val zoneId = graph[from.x][from.y].zone ?: fillZone(from, this)

        if (zoneId == graph[to.x][to.y].zone) {
            return true
        }

        if (ignoreEndObject) {
            for (direction in 0..6 step 2) {
                if (flags.canTravelInDirection(
                        to.x,
                        to.y,
                        direction
                    ) && zoneId == graph[to.x + dx(direction)][to.y + dy(direction)].zone
                ) {
                    debug { "ignoring end blocked direction: $direction" }
                    return true
                }
            }
        }

        return false
    }

    private fun fillZone(from: Point, map: GridMap): Int {
        val fromVertex = map.graph[from.x][from.y]
        assert(fromVertex.zone == null)

        val zoneId = map.zoneCount++
        debug { "new zone $zoneId" }

        var zoneSize = 0
        val frontier = mutableListOf(fromVertex)
        val seen = hashSetOf<GridVertex>()
        val startTime = System.currentTimeMillis()
        while (frontier.isNotEmpty()) {
            val vertex = frontier.removeFirst()
            for (edge in vertex.adjacentEdges) {
                val nextVertex = map.graph[vertex.x + dx(edge.direction)][vertex.y + dy(edge.direction)]
                if (seen.contains(nextVertex)) continue

                if (nextVertex.zone != null) {
                    //gets thrown when local is on a blcoked tile?, cba rn
                    throw RetryableBotException("nextVertex.zone:${nextVertex.zone} zoneId:$zoneId from: $from")
                }

                nextVertex.zone = zoneId
                frontier.add(nextVertex)
                seen.add(nextVertex)
                zoneSize++
            }
        }
        val time = System.currentTimeMillis() - startTime
        info { "new zone:$zoneId size:$zoneSize in $time ms" }
        return zoneId
    }

    private fun dx(direction: Int): Int {
        return when (direction) {
            0 -> 0
            1 -> 1
            2 -> 1
            3 -> 1
            4 -> 0
            5 -> -1
            6 -> -1
            7 -> -1
            else -> throw IllegalArgumentException("direction: $direction")
        }
    }

    private fun dy(direction: Int): Int {
        return when (direction) {
            0 -> 1
            1 -> 1
            2 -> 0
            3 -> -1
            4 -> -1
            5 -> -1
            6 -> 0
            7 -> 1
            else -> throw IllegalArgumentException("direction: $direction")
        }
    }

    internal fun Array<IntArray>.canTravelInDirection(fromX: Int, fromY: Int, dx: Int, dy: Int): Boolean {
        val fromFlag = this[fromX][fromY]

        if (dx == 1 && fromFlag and CollisionDataFlag.BLOCK_MOVEMENT_EAST != 0
            || dx == -1 && fromFlag and CollisionDataFlag.BLOCK_MOVEMENT_WEST != 0
            || dy == 1 && fromFlag and CollisionDataFlag.BLOCK_MOVEMENT_NORTH != 0
            || dy == -1 && fromFlag and CollisionDataFlag.BLOCK_MOVEMENT_SOUTH != 0
        ) return false

        //east west
        if (dx != 0) {
            val xFlag = this[fromX + dx][fromY]
            if (xFlag and CollisionDataFlag.BLOCK_MOVEMENT_FULL != 0) return false
//            if (dx == 1) {
//                if (xFlag and CollisionDataFlag.BLOCK_MOVEMENT_WEST != 0) return false
//            } else {
//                if (xFlag and CollisionDataFlag.BLOCK_MOVEMENT_EAST != 0) return false
//            }
        }

        //north south
        if (dy != 0) {
            val yFlag = this[fromX][fromY + dy]
            if (yFlag and CollisionDataFlag.BLOCK_MOVEMENT_FULL != 0) return false
//            if (dy == 1) {
//                if (yFlag and CollisionDataFlag.BLOCK_MOVEMENT_SOUTH != 0) return false
//            } else {
//                if (yFlag and CollisionDataFlag.BLOCK_MOVEMENT_NORTH != 0) return false
//            }
        }

        return true
    }

    private fun Array<IntArray>.canTravelInDirection(fromX: Int, fromY: Int, direction: Int): Boolean {
        val dx = dx(direction)
        val dy = dy(direction)
        return canTravelInDirection(fromX, fromY, dx, dy)
    }
}
