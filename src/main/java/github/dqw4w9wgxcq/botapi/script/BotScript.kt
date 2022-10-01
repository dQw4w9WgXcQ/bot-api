package github.dqw4w9wgxcq.botapi.script

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.Events
import github.dqw4w9wgxcq.botapi.antiban.Antiban
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.loader.IBotScript
import org.slf4j.event.Level
import java.io.IOException
import java.util.concurrent.ExecutionException
import kotlin.system.exitProcess

abstract class BotScript : IBotScript {
    companion object {
        var nextLoopDelay: Int? = null

        @Volatile
        var looping = false
    }

    protected abstract fun loop()

    protected open fun onStart() {
        debug { "default onStart" }
    }

    protected open fun onFinish() {
        debug { "default onFinish" }
    }

    protected open fun cleanUpFatal(e: Throwable) {
        debug { "default cleanUpFatal" }
    }

    override fun stopLooping() {
        info { "stopLooping" }
        looping = false
    }

    var loopCount = 0

    override fun run() {
        fun reset() {
            Events.clear()
            BlockingEvents.reset()
        }

        try {
            reset()//unnecessary probably

            looping = true

            onStart()

            Antiban.active = true

            var failCount = 0
            while (looping) {
                debug { "START OF LOOP$loopCount" }
                loopCount++

                if (!Client.clientThread.isAlive) {
                    warn { "game thread dead" }
                    exitProcess(401)
                }

                nextLoopDelay = null

                try {
                    if (BlockingEvents.checkBlocked()) {
                        debug { "a blocking event triggered" }
                    } else if (!looping) {
                        debug { "not looping after blocking events" }
                    } else {
                        loop()
                        failCount = 0
                    }
                } catch (e: Exception) {
                    debug { "exception in loop: $e" }

                    var unwrappedE: Exception = e
                    var i = 0//only for log
                    while (unwrappedE is ExecutionException) {
                        debug { "$i unwrapping $unwrappedE" }
                        if (unwrappedE.cause == null) {
                            warn { "ExecutionException cause null $unwrappedE" }
                            break
                        }

                        if (unwrappedE.cause !is Exception) {
                            throw Exception("while unwrapping cause is not an exception, cause:${unwrappedE.cause}", e)
                        }

                        unwrappedE = unwrappedE.cause as Exception
                        i++
                    }

                    nextLoopDelay = when {
                        unwrappedE is FatalException || unwrappedE is IllegalStateException -> {
                            throw e
                        }
//                        failCount == 0 -> {//everything not fatal gets retried once
//                            nextLoopDelay
//                        }
                        unwrappedE is RetryableBotException && failCount < unwrappedE.retries -> {
                            if (failCount == 0) {
                                nextLoopDelay
                            } else {
                                5000
                            }
                        }

                        unwrappedE is IOException && failCount < 20 -> {
                            if (failCount < 10) {
                                10000
                            } else {
                                60000
                            }
                        }

                        else -> throw e
                    }

                    if (failCount == 0) {
                        if (e is SilentBotException) {
                            Log.log(Level.DEBUG, e) { "silent exception in loop" }
                        } else {
                            warn(e) { "exception in loop" }
                        }
                    } else {
                        info { "$failCount fails, $e" }
                    }
                    failCount++
                }

                debug { "handling nextLoopDelay: $nextLoopDelay looping: $looping" }
                if (looping) {
                    val nextLoopDelay = nextLoopDelay
                    when {
                        nextLoopDelay == null -> {
                            wait(750, 1500)
                        }

                        nextLoopDelay < 0 -> {
                            info { "nextLoopDelay is $nextLoopDelay stopping" }
                            stopLooping()
                        }

                        else -> {
                            debug { "nextLoopDelay $nextLoopDelay" }
                            wait(nextLoopDelay)
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            warn(t) { "fatal in script" }
            try {
                cleanUpFatal(t)
            } catch (e: Exception) {
                warn(e) { "cleanUpFatal is throwing stuff lol" }
                throw Exception("cleanUpFatal is throwing stuff", e)
            }
        } finally {
            debug { "turning off antiban, clearing events, paint" }
            looping = false//some other threads might check this so ensure its off
            Antiban.active = false
            reset()
        }
    }
}
