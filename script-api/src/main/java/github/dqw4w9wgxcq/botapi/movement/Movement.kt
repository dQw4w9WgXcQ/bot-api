package github.dqw4w9wgxcq.botapi.movement

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.antiban.Profile
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.entities.Players
import github.dqw4w9wgxcq.botapi.entities.TileObjects
import github.dqw4w9wgxcq.botapi.interact.Interact
import github.dqw4w9wgxcq.botapi.itemcontainer.Inventory
import github.dqw4w9wgxcq.botapi.movement.pathfinding.local.LocalPathfinding
import github.dqw4w9wgxcq.botapi.movement.pathfinding.local.LocalPathfinding.canReach
import github.dqw4w9wgxcq.botapi.movement.pathfinding.local.LocalPathfinding.canTravelInDirection
import github.dqw4w9wgxcq.botapi.widget.Dialog
import github.dqw4w9wgxcq.botapi.widget.Widgets
import github.dqw4w9wgxcq.botapi.wrappers.entity.tile.`object`.WallObject
import net.runelite.api.ItemID
import net.runelite.api.Point
import net.runelite.api.Varbits
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.WidgetInfo

object Movement {
    val runThreshold = Profile.getInt("run threshold", 30) + 60
    val stamThreshold = Profile.getInt("stam threshold", 10) + 10

    fun runEnergy(): Int = Client.energy
    fun runEnabled(): Boolean = Client.getVarpValue(173) == 1
    fun getStaminaConfig(): Int = Client.getVarbitValue(Varbits.RUN_SLOWED_DEPLETION_ACTIVE)
    fun isStaminaActive(): Boolean = getStaminaConfig() > 0
    fun isMoving(): Boolean = Players.local().isMoving && !Dialog.canContinue()

    fun walk(to: Point) {
        if (walkOrOpenDoor(getDoorOrWalkPoint(to, false) ?: to)) {
            debug { "walking a second time" }
            walkOrOpenDoor(getDoorOrWalkPoint(to, false) ?: to)
        }
    }

    fun walk(to: WorldPoint) {
        val plane = Client.plane
        if (to.plane != plane) {
            throw RetryException("to.plane:${to.plane} != plane:$plane")
        }

        val scenePoint = to.toScene()
        if (!scenePoint.isInTrimmedScene()) {
            //todo walk to closest tile in scene
            throw RetryException("$to not in trimmed scene")
        }

        walk(scenePoint)
    }

    fun walk(x: Int, y: Int, plane: Int = Client.plane) {
        walk(WorldPoint(x, y, plane))
    }

    fun toggleRun(on: Boolean, waitFor: Boolean = true) {
        if (on == runEnabled()) return
        Widgets.get(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB).interact("Toggle run")
        if (waitFor) {
            waitUntil { on == runEnabled() }
        }
    }

    fun checkStam() {
        if (runEnergy() > stamThreshold) {
            return
        }

        val stam = Inventory.getOrNull(
            ItemID.STAMINA_POTION1,
            ItemID.STAMINA_POTION2,
            ItemID.STAMINA_POTION3,
            ItemID.STAMINA_POTION4
        )

        if (stam != null && !stam.name.contains("member", true)) {
            stam.interact("Drink")
        }
    }

    fun checkRun() {
        if (runEnabled()) {
            return
        }

        if (isStaminaActive()) {
            toggleRun(true)
            return
        }

        if (runEnergy() >= runThreshold) {
            toggleRun(true)
            return
        }
    }

    fun checkDoor(to: Point, ignoreEndObject: Boolean): Boolean {
        if (walkOrOpenDoor(getDoorOrWalkPoint(to, ignoreEndObject) ?: return true)) {
            info { "walking a second time bc door" }
            walkOrOpenDoor(getDoorOrWalkPoint(to, ignoreEndObject) ?: return true)
        }

        return false
    }

    //returns tile to walk or door to open
    private fun getDoorOrWalkPoint(to: Point, ignoreEndObject: Boolean): Any? {
        val map = LocalPathfinding.map
        val flags = Client.collisionMaps!![Client.plane].flags!!
        val myPoint = Players.local().sceneLocation

        if (!map.canReach(to, myPoint, ignoreEndObject)) {
            throw RetryException("not reachable to:$to ${to.toWorld()} from:$myPoint ${myPoint.toWorld()}")
        }

        val path = LocalPathfinding.findPathSkipReachableCheck(to, myPoint, ignoreEndObject, map)

        debug { "path size:${path.size}" }

        val points = ArrayDeque(path)

        while (points.isNotEmpty()) {
            val curr = points.removeFirst()
            val next = points.firstOrNull() ?: break

            debug { "curr $curr next $next" }

            if (flags.canTravelInDirection(curr.x, curr.y, next.x - curr.x, next.y - curr.y)) {
                continue
            }

            debug { "prob have a door" }

            val door = let {
                var o = TileObjects.firstAtOrNull(curr) {
                    it is WallObject && LocalPathfinding.WallObstacle.DOOR.test(it)
                }
                if (o == null) {
                    o = TileObjects.firstAtOrNull(next) {
                        it is WallObject && LocalPathfinding.WallObstacle.DOOR.test(it)
                    }
                }
                o
            }

            if (door == null) {
                debug { "no door" }
                if ((!map.flags.contentEquals(Client.collisionMaps!![Client.plane].flags))) {
                    warn { "flags are actually equal, can happen if the tiles changed then changed back?" }
                }

                throw RetryException("blocked but theres no door, can happen if map changed.  should never happen if current flags are passed")
            }

            //the game doesn't load door state beyond ~30 tiles? idk how it works exactly
            val distance = door.distance()
            debug { "door distance:$distance" }
            if (distance >= 25) {
                info { "door is far:$distance" }
                return curr
            }

            return door
        }

        return null
    }

    //arg is either a point or door
    //return true if should repeat(we opened a door close by)
    private fun walkOrOpenDoor(doorOrTile: Any): Boolean {
        val walkPoint = when (doorOrTile) {
            is Point -> {
                debug { "point ${doorOrTile.toWorld().toDetailedString()}" }
                doorOrTile
            }

            is WallObject -> {
                debug { "wall ${doorOrTile.name} ${doorOrTile.id} ${doorOrTile.worldLocation.toDetailedString()}" }
                doorOrTile.sceneLocation
            }

            else -> throw IllegalArgumentException("not a point or wallobject $doorOrTile ${doorOrTile::class.java.name}")
        }

        val destination = Client.localDestinationLocation?.toScene()
        val distance = walkPoint.distance()

        debug { "distance: $distance, destination: $destination, off: ${destination?.distance(walkPoint)}" }

        if (doorOrTile is WallObject && distance < 25) {//real door state isnt received from server unless within ~30 tiles
            val doorType = LocalPathfinding.WallObstacle.values().first { it.test(doorOrTile) }

            Interact.withEntity(doorOrTile) { doorType.index0Actions.contains(it) }

            when (doorType) {
                LocalPathfinding.WallObstacle.DOOR -> {
                    if (distance < 4) {
                        val id = doorOrTile.id
                        waitUntil(3000) { TileObjects.firstAtOrNull(walkPoint, byId(id)) == null }
                        return true
                    }
                }

                LocalPathfinding.WallObstacle.WEB -> {
                    TODO("waitUntil { Players.local().animation == slashAnimation }")
                }
            }
        } else {
            debug { "interacting walk scene:$walkPoint world:${walkPoint.toWorld()}" }
            Interact.walk(walkPoint)
            waitUntil(condition = { isMoving() }.desc("moving"))
        }

        checkStam()
        checkRun()
        return false
    }
}
