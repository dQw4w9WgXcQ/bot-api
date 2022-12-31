package github.dqw4w9wgxcq.botapi.commons

import github.dqw4w9wgxcq.botapi.commons.Log.log
import org.slf4j.event.Level
import java.time.LocalTime
import java.time.temporal.ChronoUnit

fun warn(e: Throwable? = null, lazyMessage: () -> Any?) {
    log(Level.WARN, e, lazyMessage)
}

fun info(lazyMessage: () -> Any?) {
    log(Level.INFO, null, lazyMessage)
}

fun debug(lazyMessage: () -> Any?) {
    log(Level.DEBUG, null, lazyMessage)
}

//needs concrete class to get stack context without hardcoding class name
object Log {
    data class Policy(val packagePath: String, val level: Level)

    class Config(val defaultLogLevel: Level = Level.INFO) {
        private val policies = mutableListOf<Policy>()

        fun addPolicy(policy: Policy) {
            policies.add(policy)
        }

        fun addPolicy(path: String, level: Level) {
            addPolicy(Policy(path, level))
        }

        fun addPolicy(clazz: Class<*>, level: Level) {
            addPolicy(clazz.name, level)
        }

        fun getPolicyLevel(classLabel: Class<*>): Level {
            var mostSpecificPolicy = Policy("", config.defaultLogLevel)
            val topClassName = classLabel.name
            for (policy in config.policies) {
                if (topClassName.startsWith(policy.packagePath) && mostSpecificPolicy.packagePath.length <= policy.packagePath.length) {
                    mostSpecificPolicy = policy
                }
            }

            return mostSpecificPolicy.level
        }
    }

    private object GetCallingClass : SecurityManager() {
        //cant b anon class cuz classContext is protected and need real class to get stack context
        fun get(): Class<*> {
            val classContext = classContext
            //cba rn
            return classContext.first { it.name != Log::class.java.name + "Kt" && it != Config::class.java && it != Log::class.java && it != GetCallingClass::class.java }
        }
    }

    var config = Config()

    fun log(level: Level, throwable: Throwable?, lazyMessage: () -> Any?) {
        val classLabel = GetCallingClass.get()

        if (config.getPolicyLevel(classLabel) < level) {
            return
        }

        val message = lazyMessage()

        val time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS)
        val threadName = Thread.currentThread().name
        //val packageInitials = classLabel.`package`.name.split(".").map { it[0] }.joinToString(separator = ".")
        val printName = classLabel.name.substringAfterLast(".")
        var fullMessage = if (message is Throwable) {
            message.stackTraceToString()
        } else {
            message.toString()
        }
        if (throwable != null) {
            fullMessage += "\n" + throwable.stackTraceToString()
        }

        val formattedMessage = "$time $threadName $level $printName $fullMessage"

        println(formattedMessage)
    }

    //cant go in top level because conflicts
    fun error(throwable: Throwable? = null, lazyMessage: () -> Any?) {
        log(Level.ERROR, throwable, lazyMessage)
    }
}
