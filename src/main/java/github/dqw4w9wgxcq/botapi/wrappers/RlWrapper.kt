package github.dqw4w9wgxcq.botapi.wrappers

abstract class RlWrapper<RL>(val rl: RL) {
    override fun equals(other: Any?): Boolean {
        if (other !is RlWrapper<*>) return false
        return rl == other.rl//cant b pointer comparison bc Item
    }

    override fun hashCode(): Int {
        return rl.hashCode()
    }
}
