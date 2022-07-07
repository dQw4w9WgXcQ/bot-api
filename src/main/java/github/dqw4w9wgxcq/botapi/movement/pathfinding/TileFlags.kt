package github.dqw4w9wgxcq.botapi.movement.pathfinding

object TileFlags {
    const val NW = 1
    const val N = 1 shl 1
    const val NE = 1 shl 2
    const val E = 1 shl 3
    const val SE = 1 shl 4
    const val S = 1 shl 5
    const val SW = 1 shl 6
    const val W = 1 shl 7

    const val OBJECT = 1 shl 8
    const val DECORATION = 1 shl 18
    const val FLOOR = 1 shl 21
    const val FULL = OBJECT or DECORATION or FLOOR
}