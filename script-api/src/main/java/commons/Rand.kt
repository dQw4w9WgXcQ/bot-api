package github.dqw4w9wgxcq.botapi.commons

import kotlin.random.Random

object Rand {
    var bias = Random.nextDouble(0.2, 0.8)
        set(value) {
            require(value in 0.2..0.8) { "bias must be [0.2-0.8]" }
            info { "setting bias $value" }
            field = value
        }

    init {
        info { "bias is $bias" }
    }

    fun nextInt(inclusive: IntRange): Int {
        return nextInt(inclusive.first, inclusive.last + 1)
    }

    fun nextInt(until: Int): Int {
        require(until >= 1) { "until must be >= 1" }

        return (nextDouble() * until).toInt()
    }

    fun nextDouble(bias: Double = this.bias): Double {
        require(bias in 0.2..0.8) { "bias must be [0.2-0.8]" }
        val randomBias = bias + Random.nextDouble(-0.1, 0.1)
        //cba rn
        val out = if (Random.nextDouble() < randomBias) {
            (1 - Random.nextDouble() * Random.nextDouble()) * randomBias
        } else {
            Random.nextDouble() * Random.nextDouble() * (1 - randomBias) + randomBias
        }

        require(out >= 0.0 && out < 1.0) { "must be in [0.0-1.0)" }

        return out
    }

    fun nextBoolean(): Boolean {
        return Random.nextDouble() < bias
    }

    fun nextInt(from: Int, until: Int): Int {
        return from + nextInt(until - from)
    }

    fun <T> element(col: List<T>): T {
        require(col.isNotEmpty()) { "col must not be empty" }
        return col[Random.nextInt(col.size)]
    }
}
