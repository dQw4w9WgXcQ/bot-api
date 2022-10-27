package github.dqw4w9wgxcq.botapi.script

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.Events
import github.dqw4w9wgxcq.botapi.antiban.Antiban
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.loader.BotApi
import github.dqw4w9wgxcq.botapi.loader.IBotScript
import org.slf4j.event.Level
import java.io.FileNotFoundException
import java.io.IOException
import java.net.MalformedURLException
import java.util.concurrent.ExecutionException
import kotlin.system.exitProcess

abstract class BotScript : IBotScript {
    companion object {
        @Volatile
        var nextLoopDelay: Int? = null
            set(value) {
                require(value == null || value >= 0) { "$value" }
                field = value
            }

        @Volatile
        var looping = false
    }

    protected abstract fun loop()

    protected open fun onStart() {
        debug { "default onStart" }
    }

    protected open fun cleanUpFatal(e: Throwable) {
        debug { "default cleanUpFatal" }
    }

    final override fun stopLooping() {
        debug { "stopLooping" }
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

            Events.register(this)

            var failCount = 0
            while (looping) {
                debug { "START OF LOOP$loopCount" }
                loopCount++

                if (!Client.clientThread.isAlive) {
                    warn { "game thread dead" }
                    exitProcess(201)
                }

                nextLoopDelay = null

                try {
                    if (BlockingEvents.checkBlocked()) {
                        debug { "blocking event triggered" }
                    } else if (!looping) {
                        debug { "not looping after checking blocking events" }
                    } else {
                        loop()
                        failCount = 0
                    }
                } catch (e: Exception) {
                    @Suppress("NAME_SHADOWING")
                    var e = e

                    debug { "exception in loop: $e" }

                    while (e is ExecutionException) {
                        debug { "unwrapping $e" }

                        val cause = e.cause

                        if (cause == null) {
                            warn { "ExecutionException cause null $e" }
                            break
                        }

                        if (cause !is Exception) {
                            throw Exception("cause is not an exception:$cause", e)
                        }

                        e = cause
                    }

                    nextLoopDelay = when {
                        e is RetryableBotException && failCount < e.retries -> {
                            if (failCount < 2) {
                                1000
                            } else {
                                5000
                            }
                        }

                        e is FileNotFoundException || e is MalformedURLException -> throw e

                        e is IOException && failCount < 20 -> {
                            if (failCount < 2) {
                                nextLoopDelay
                            } else if (failCount < 5) {
                                10000
                            } else {
                                60000
                            }
                        }

                        else -> throw e
                    }

                    if (failCount == 0) {
                        if (e is SilentBotException) {
                            Log.log(Level.DEBUG, e) { "exception in loop" }
                        } else {
                            warn(e) { "exception in loop" }
                        }
                    } else {
                        if (e is SilentBotException) {
                            debug { "$failCount fails, $e" }
                        } else {
                            info { "$failCount fails, $e" }
                        }
                    }

                    failCount++
                }

                if (looping) {
                    debug { "nextLoopDelay:$nextLoopDelay" }
                    when (nextLoopDelay) {
                        null -> {
                            wait(750, 1500)
                        }

                        else -> {
                            wait(nextLoopDelay!!)
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            warn(t) { "fatal in script" }

            BotApi.toggleFrame(true)

            try {
                cleanUpFatal(t)
            } catch (e: Exception) {
                warn(e) { "cleanUpFatal errored" }
                throw Exception("cleanUpFatal errored", e)
            }
        } finally {
            debug { "turning off antiban, clearing events, paint" }
            looping = false//some other threads might check this so ensure its off
            Antiban.active = false
            reset()
        }
    }
}
