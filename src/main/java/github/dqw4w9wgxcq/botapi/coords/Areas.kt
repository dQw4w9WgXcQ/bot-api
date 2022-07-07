package github.dqw4w9wgxcq.botapi.coords

import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.sceneentities.actors.players.Players
import github.dqw4w9wgxcq.botapi.wrappers.Locatable
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint

object Areas {
    fun World(x1: Int, y1: Int, x2: Int, y2: Int, plane: Int): WorldArea {
        val xMin: Int
        val xMax: Int
        if (x1 < x2) {
            xMin = x1
            xMax = x2
        } else {
            xMin = x2
            xMax = x1
        }

        val yMin: Int
        val yMax: Int
        if (y1 < y2) {
            yMin = y1
            yMax = y2
        } else {
            yMin = y2
            yMax = y1
        }

        val width = xMax - xMin + 1
        val height = yMax - yMin + 1

        return WorldArea(xMin, yMin, width, height, plane)
    }
}

private val WorldArea.x2: Int
    get() = x + width - 1
private val WorldArea.y2: Int
    get() = y + height - 1
private val WorldArea.xRange: IntRange
    get() = x..x2
private val WorldArea.yRange: IntRange
    get() = y..y2

fun WorldArea.contains(locatable: Locatable): Boolean {
    return contains(locatable.worldLocation)
}

fun WorldArea.containsLocal(): Boolean {
    return contains(Players.local())
}

fun WorldArea.randomPoint(): WorldPoint {
    return WorldPoint(Rand.nextInt(xRange), Rand.nextInt(yRange), plane)
}

fun WorldArea.toPlane(plane: Int): WorldArea {
    return WorldArea(x, y, width, height, plane)
}
