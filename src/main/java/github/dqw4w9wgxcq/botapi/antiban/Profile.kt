package github.dqw4w9wgxcq.botapi.antiban

import github.dqw4w9wgxcq.botapi.commons.info
import kotlin.random.Random

object Profile {
    private var _hash: Int? = null
    private val hash: Int
        get() {
            if (_hash == null) {
                val acc = System.getProperty("bot.acc")
                if (acc != null) {
                    this._hash = acc.hashCode()
                    info { "Profile initialized with acc: $acc hash: $_hash" }
                } else {
                    _hash = Random.nextInt()
                    info { "no acc, set hash to random $_hash" }
                }
            }

            return _hash!!
        }

    fun newRandom(key: Int): Random {
        return Random(hash * key)
    }

    fun newRandom(key: String): Random {
        return newRandom(key.hashCode())
    }

    fun newRandom(): Random {
        return newRandom(hash)
    }

    private fun getInt(key: Int, until: Int): Int {
        return newRandom(key).nextInt(until)
    }

    fun getInt(key: String, until: Int): Int {
        return getInt(key.hashCode(), until)
    }

    fun getInt(key: String, from: Int, until: Int): Int {
        return from + getInt(key, until - from)
    }

    private fun getBoolean(key: Int): Boolean {
        return newRandom(key).nextBoolean()
    }

    private fun <T> pick(key: Int, from: List<T>): T {
        return from[getInt(key, from.size)]
    }

    fun getBoolean(key: String): Boolean {
        return getBoolean(key.hashCode())
    }

    fun <T> pick(key: String, from: List<T>): T {
        return pick(key.hashCode(), from)
    }
}