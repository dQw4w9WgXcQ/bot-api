package github.dqw4w9wgxcq.botapi.commons

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.entities.Players
import github.dqw4w9wgxcq.botapi.loader.RuneliteContext
import github.dqw4w9wgxcq.botapi.movement.pathfinding.local.LocalPathfinding
import github.dqw4w9wgxcq.botapi.script.BotScript
import github.dqw4w9wgxcq.botapi.wrappers.Identifiable
import github.dqw4w9wgxcq.botapi.wrappers.Interactable
import github.dqw4w9wgxcq.botapi.wrappers.Locatable
import github.dqw4w9wgxcq.botapi.wrappers.Nameable
import github.dqw4w9wgxcq.botapi.wrappers.entity.actor.NPC
import github.dqw4w9wgxcq.botapi.wrappers.entity.tile.`object`.TileObject
import github.dqw4w9wgxcq.botapi.wrappers.item.Item
import net.runelite.api.Constants
import net.runelite.api.Point
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint
import java.awt.Rectangle
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.math.abs
import kotlin.math.hypot

const val ONE_MINUTE = 60_000
const val ONE_HOUR = 60 * ONE_MINUTE
const val ONE_DAY = 24 * ONE_HOUR

fun <T> onGameThreadAsync(runnable: () -> T): Future<T> {
    val future = FutureTask(runnable)

    RuneliteContext.getClientThread().invoke(future)

    return future
}

fun <T> onGameThread(runnable: () -> T): T {
    val future = onGameThreadAsync(runnable)

    return try {
        future.get(3000L, TimeUnit.MILLISECONDS)//3 sec bc 1 sec was causing issues on big lag idk
    } catch (e: TimeoutException) {
        throw RetryException("Timed out on game thread", e)
    }
}

fun WorldPoint.distance(): Int {
    return distanceTo(Players.local().worldLocation)
}

fun WorldPoint.isNear(): Boolean {
    return distance() < 14 && LocalPathfinding.canReach(this, true)
}

fun WorldPoint.isNotNear(): Boolean {
    return !isNear()
}

fun WorldPoint.toScene(baseX: Int = Client.baseX, baseY: Int = Client.baseY): Point {
    return Point(x - baseX, y - baseY)
}

fun WorldPoint.toPlane(plane: Int = Client.plane): WorldPoint {
    return WorldPoint(x, y, plane)
}

const val SCENE_FROM = 1
const val SCENE_UNTIL = Constants.SCENE_SIZE - 5
fun Point.isInTrimmedScene(): Boolean {
    return this.x >= SCENE_FROM && this.y >= SCENE_FROM && this.x < SCENE_UNTIL && this.y < SCENE_UNTIL
}

fun WorldPoint.isInTrimmedScene(): Boolean {
    return this.toScene().isInTrimmedScene()
}

fun WorldPoint.toDetailedString(): String {
    val scene = toScene()
    return "($x,$y,$plane) scene:(${scene.x},${scene.y})"
}

fun LocalPoint.toScene(): Point {
    return Point(sceneX, sceneY)
}

fun Point.toWorld(baseX: Int = Client.baseX, baseY: Int = Client.baseY, plane: Int = Client.plane): WorldPoint {
    return WorldPoint(x + baseX, y + baseY, plane)
}

fun Point.distance(to: Point): Int {//manhattan distance, runelite Point.distanceTo(Point) uses pythag
    return maxOf(abs(x - to.x), abs(y - to.y))
}

fun Point.pythagDist(to: Point): Double {
    return hypot(abs(x.toDouble() - to.x.toDouble()), abs(y.toDouble() - to.y.toDouble()))
}

fun Point.distance(to: Locatable = Players.local()): Int {//manhattan distance, Point.distanceTo(Point) uses pythag
    return distance(to.sceneLocation)
}

fun Point.toAwt(): java.awt.Point {
    return java.awt.Point(x, y)
}

fun java.awt.Point.toRl(): Point {
    return Point(x, y)
}

fun Rectangle.randomPoint(): java.awt.Point {
    return java.awt.Point(Rand.nextInt(x, x + width), Rand.nextInt(y, y + height))
}

fun inGameBounds(point: java.awt.Point): Boolean {
    return point.x >= 0 && point.y >= 0 && point.x < Client.canvasWidth && point.y < Client.canvasHeight
}

object Wait {
    const val defaultPollRate = 50
    const val defaultTimeout = 5000
}

fun wait(millis: Int) {
    Thread.sleep(millis.toLong())
}

fun wait(from: Int, until: Int) {
    wait(Rand.nextInt(from, until))
}

fun <T> waitUntilCondition(
    timeout: Int = Wait.defaultTimeout,
    pollRate: Int = Wait.defaultPollRate,
    supply: () -> T?,
    condition: (T?) -> Boolean,
): T? {
    require(timeout > pollRate) { "timeout must be > pollRate" }

    val start = System.currentTimeMillis()
    do {
        if (!BotScript.looping) {
            throw SilentException("bot script not looping")
        }

        val supplied = supply()
        if (condition(supplied)) {
            debug { "$supply validated $condition after ${System.currentTimeMillis() - start}ms" }
            return supplied
        }

        Thread.sleep(pollRate.toLong())
    } while (System.currentTimeMillis() < start + timeout)

    @Suppress("UNCHECKED_CAST")//no clue this is necessary
    throw WaitTimeoutException(
        timeout,
        pollRate,
        supply,
        condition as (Any?) -> Boolean,
    )
}

fun <T> waitUntilNotNull(
    timeout: Int = Wait.defaultTimeout,
    pollRate: Int = Wait.defaultPollRate,
    supply: () -> T?,
): T {
    return waitUntilCondition(
        timeout,
        pollRate,
        supply = supply,
        condition = { it: T? -> it != null }.desc("not null")
    )!!
}

fun waitUntil(
    timeout: Int = Wait.defaultTimeout,
    pollRate: Int = Wait.defaultPollRate,
    condition: () -> Boolean,
) {
    waitUntilCondition(
        timeout,
        pollRate,
        supply = condition,
        condition = { it: Boolean? -> it!! }.desc("true")
    )
}

fun waitUntilWithConfirm(
    timeout: Int = Wait.defaultTimeout,
    pollRate: Int = Wait.defaultPollRate,
    condition: () -> Boolean,
): Boolean {
    return try {
        waitUntil(timeout, pollRate, condition)
        true
    } catch (e: WaitTimeoutException) {
        false
    }
}

fun <T> ((T) -> Boolean).and(that: (T) -> Boolean): (T) -> Boolean {
    val self = this
    return object : (T) -> Boolean {
        override fun invoke(it: T): Boolean {
            return this@and(it) && that(it)
        }

        override fun toString(): String {
            return "($self & $that)"
        }
    }
}

fun <T> ((T) -> Boolean).or(that: (T) -> Boolean): (T) -> Boolean {
    val self = this
    return object : (T) -> Boolean {
        override fun invoke(it: T): Boolean {
            return this@or(it) || that(it)
        }

        override fun toString(): String {
            return "($self | $that)"
        }
    }
}

fun (() -> Boolean).or(that: () -> Boolean): () -> Boolean {
    val self = this
    return object : () -> Boolean {
        override fun invoke(): Boolean {
            return this@or() || that()
        }

        override fun toString(): String {
            return "($self | $that)"
        }
    }
}

fun <T> ((T) -> Boolean).negate(): (T) -> Boolean {
    val self = this

    return object : (T) -> Boolean {
        override fun invoke(it: T): Boolean {
            return !this@negate(it)
        }

        override fun toString(): String {
            return "negate($self)"
        }
    }
}

fun byName(vararg ignoreCase: String): (Nameable) -> Boolean {
    return object : (Nameable) -> Boolean {
        override fun invoke(nameable: Nameable): Boolean {
            val name = nameable.name
            return ignoreCase.any { name.equals(it, true) }
        }

        override fun toString(): String {
            return "name[${ignoreCase.joinToString(",")}]"
        }
    }
}

fun byEquals(vararg ignoreCase: String): (String) -> Boolean {
    return object : (String) -> Boolean {
        override fun invoke(s: String): Boolean {
            return ignoreCase.any { it.equals(s, true) }
        }

        override fun toString(): String {
            return "equals[${ignoreCase.joinToString(",")}]"
        }
    }
}

fun byContains(vararg ignoreCase: String): (String) -> Boolean {
    return object : (String) -> Boolean {
        override fun invoke(s: String): Boolean {
            return ignoreCase.any { it.contains(s, true) }
        }

        override fun toString(): String {
            return "contains[${ignoreCase.joinToString(",")}]"
        }
    }
}

fun byPrefix(vararg ignoreCase: String): (Nameable) -> Boolean {
    return object : (Nameable) -> Boolean {
        override fun invoke(nameable: Nameable): Boolean {
            val name = nameable.name
            return ignoreCase.any { name.startsWith(it, true) }
        }

        override fun toString(): String {
            return "prefix[${ignoreCase.joinToString(",")}]"
        }
    }
}

fun bySuffix(vararg ignoreCase: String): (Nameable) -> Boolean {
    return object : (Nameable) -> Boolean {
        override fun invoke(nameable: Nameable): Boolean {
            val name = nameable.name
            return ignoreCase.any { name.endsWith(it, true) }
        }

        override fun toString(): String {
            return "suffix[${ignoreCase.joinToString(",")}]"
        }
    }
}

private enum class IdentifiableTypes {
    ITEM, NPC, OBJECT, UNKNOWN
}

fun byId(vararg ids: Int): (Identifiable) -> Boolean {
    return object : (Identifiable) -> Boolean {
        var typeWas: IdentifiableTypes? = null
        override fun invoke(it: Identifiable): Boolean {
            if (typeWas == null) {
                typeWas = when (it) {
                    is Item -> IdentifiableTypes.ITEM
                    is NPC -> IdentifiableTypes.NPC
                    is TileObject<*> -> IdentifiableTypes.OBJECT
                    else -> IdentifiableTypes.UNKNOWN
                }
            }
            return ids.contains(it.id)
        }

        override fun toString(): String {
            var label = "id"
            val s = if (typeWas != null && typeWas != IdentifiableTypes.UNKNOWN) {
                val nameMapper = when (typeWas) {
                    IdentifiableTypes.ITEM -> {
                        { it: Int -> Client.getItemDefinition(it).name }
                    }

                    IdentifiableTypes.NPC -> {
                        { it: Int -> Client.getNpcDefinition(it).name }
                    }

                    IdentifiableTypes.OBJECT -> {
                        { it: Int -> Client.getObjectDefinition(it).name }
                    }

                    else -> throw FatalException("fuck")
                }
                label = typeWas!!.name.lowercase()
                ids.joinToString { "$it:${nameMapper(it)}" }
            } else {
                ids.joinToString()
            }
            return "$label[$s]"
        }
    }
    //return { it: Identifiable -> ids.contains(it.id) }.desc("id[${ids.joinToString(",")}]")
}

fun byIdIgnoreNote(vararg ids: Int): (Item) -> Boolean {
    return object : (Item) -> Boolean {
        override fun invoke(item: Item): Boolean {
            return ids.contains(item.id) || ids.contains(item.linkedNoteId)
        }

        override fun toString(): String {
            val names = onGameThread {
                ids.map { Client.getItemDefinition(it).name ?: throw Exception("item with id:$it name null") }
            }
            return "idIgnoreNote[${names.joinToString(",")}]"
        }
    }
}

fun byAction(vararg ignoreCase: String): (Interactable) -> Boolean {
    return { interactable: Interactable -> ignoreCase.any { interactable.hasAction(it) } }
        .desc("byAction[${ignoreCase.joinToString(",")}]")
}

fun <T, U> ((T) -> U).desc(toString: String): (T) -> U {
    return object : (T) -> U {
        override fun invoke(it: T): U {
            return this@desc(it)
        }

        override fun toString(): String {
            return toString
        }
    }
}

fun <T> (() -> T).desc(toString: String): () -> T {
    return object : () -> T {
        override fun invoke(): T {
            return this@desc()
        }

        override fun toString(): String {
            return toString
        }
    }
}
