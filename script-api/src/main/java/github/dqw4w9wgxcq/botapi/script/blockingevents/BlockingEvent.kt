package github.dqw4w9wgxcq.botapi.script.blockingevents

abstract class BlockingEvent {
    abstract fun checkBlocked(): Boolean
}