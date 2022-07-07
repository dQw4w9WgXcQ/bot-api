package github.dqw4w9wgxcq.botapi.loader

import org.slf4j.LoggerFactory

class ScriptThread : Thread("script") {
    private val log = LoggerFactory.getLogger(ScriptThread::class.java)

    @Volatile
    var activeScript: IBotScript? = null
        private set

    private val mutex = Object()

    override fun run() {
        while (!isInterrupted) {
            synchronized(mutex) {
                if (activeScript == null) {
                    mutex.wait()
                }
            }

            try {
                activeScript!!.run()
            } catch (e: Exception) {//just catch all errors bc kotlin t0do error etc.
                log.warn("exception in script run method", e)
            } catch (e: Error) {
                if (e is VirtualMachineError) {
                    log.debug("script thread got virtual machine error", e)
                    throw e
                }

                log.warn("error in script method", e)
            }

            activeScript = null
        }
    }

    fun offer(script: IBotScript): Boolean {
        synchronized(mutex) {
            if (activeScript != null) {
                return false
            }

            activeScript = script
            mutex.notify()
        }

        return true
    }
}
