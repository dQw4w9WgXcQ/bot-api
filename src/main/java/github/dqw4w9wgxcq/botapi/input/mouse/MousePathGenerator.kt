package github.dqw4w9wgxcq.botapi.input.mouse

import github.dqw4w9wgxcq.botapi.commons.RetryableBotException
import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.commons.info
import github.dqw4w9wgxcq.botapi.loader.BotApi
import java.awt.Point
import java.awt.Rectangle
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

class MousePathGenerator {
    class PathStore(val id: Int, val points: List<Point>)

    val paths: List<PathStore> by lazy {
        buildList {
            var id = 0
            File(BotApi.DIR, "mousedata.txt").forEachLine {
                val points = mutableListOf<Point>()

                for (pointString in it.split(":")) {
                    val pointSplit = pointString.split(",")
                    points.add(Point(pointSplit[0].toInt(), pointSplit[1].toInt()))
                }

                add(PathStore(++id, points))
            }
            info { "loaded $size paths" }
        }
    }

    //25 ms ticks
    fun findPath(startpoint: Point, endpoint: Point, ticks: Int, bounds: Rectangle?): List<Point> {
        require(ticks > 0) { "ticks cant b 0 or fewer $ticks" }
        if (bounds != null) {
            require(bounds.contains(startpoint)) { "bounds $bounds doesn't contain startpoint $startpoint" }
            require(bounds.contains(endpoint)) { "bounds $bounds doesn't contain endpiont $endpoint" }
        }

        val iters = 50
        outer@ for (i in 0..iters) {
            val path = retrieveSavedPath(startpoint, endpoint, ticks)
            if (bounds != null) {
                for (p in path) {
                    if (!bounds.contains(p)) {
                        continue@outer
                    }
                }
            }

            if (i > 0) {
                debug { "after $i iters found path" }
            }

            return path
        }

        throw IllegalArgumentException("after $iters iters, couldn't find a path within bounds $bounds start: $startpoint end: $endpoint ticks: $ticks")
    }

    //first point is always 0,0
    private fun retrieveSavedPath(startpoint: Point, endpoint: Point, ticks: Int): List<Point> {
        val path = paths.random()

        debug { "retrieved path id: " + path.id }

        val points = path.points

        let {
            val dx = abs(points.last().x) / 5
            val dy = abs(points.last().y) / 5
            var prevX = 0
            var prevY = 0
            for (point in points) {
                if (abs(abs(prevX) - abs(point.x)) > dx || abs(abs(prevY) - abs(point.y)) > dy) {
                    throw RetryableBotException("path id: ${path.id} big mouse move detected $prevX, ${point.x}, $prevY, ${point.y}")
                }

                prevX = point.x
                prevY = point.y
            }
        }

        val scaled = scalePath(points, endpoint.x - startpoint.x, endpoint.y - startpoint.y)
        val interpolated = interpolatePath(scaled, ticks, startpoint)
        Mouse.lastPath = interpolated.toList()
        return interpolated
    }

    private fun scalePath(path: List<Point>, dX: Int, dY: Int): List<Point> {
        val xScale = dX.toDouble() / path.last().x
        val yScale = dY.toDouble() / path.last().y

        return path.map { point -> Point((point.x * xScale).roundToInt(), (point.y * yScale).roundToInt()) }
    }

    private fun interpolatePath(path: List<Point>, newSize: Int, startpoint: Point): List<Point> {
        val out = mutableListOf<Point>()

        val scale = newSize.toDouble() / (path.size - 1)

        for (i in 1 until newSize) {//exclusive on both ends because startpoint is always 0,0 and want to hardcode endpoint
            val originalDecimal = i / scale
            val originalIndex = originalDecimal.toInt()
            val originalRemainder = originalDecimal - originalIndex

            val leftPoint = path[originalIndex]
            val rightPoint = path[originalIndex + 1]

            val xInterpol = ((rightPoint.x - leftPoint.x).toDouble() * originalRemainder) + leftPoint.x
            val yInterpol = ((rightPoint.y - leftPoint.y).toDouble() * originalRemainder) + leftPoint.y

            val interpolPoint = Point(xInterpol.toInt(), yInterpol.toInt())

            out.add(interpolPoint)
        }

        out.add(path.last())
        return out.map { Point(it.x + startpoint.x, it.y + startpoint.y) }
    }
}
