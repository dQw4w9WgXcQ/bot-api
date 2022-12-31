package github.dqw4w9wgxcq.botapi.input

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.Rand

object Focus {
    @Volatile
    var blockLoseFocusTime: Long = 0

    @Volatile
    var blockGainFocusTime: Long = 0

    fun require(duration: Int = Rand.nextInt(250, 500)) {
        while (true) {
            val diff = synchronized(this) {
                val currTime = System.currentTimeMillis()
                val diff = blockGainFocusTime - currTime
                if (diff <= 0) {
                    blockLoseFocusTime = currTime + duration
                }

                diff
            }

            if (diff <= 0) {
                break
            }

            Thread.sleep(diff)
        }

        if (!Client.hasFocus()) {
            CanvasInput.focusGained()
        }
    }

    fun lose(lockDuration: Int = Rand.nextInt(250, 500)) {
        while (true) {
            val dif = synchronized(this) {
                val currTime = System.currentTimeMillis()
                val diff = blockLoseFocusTime - currTime
                if (diff <= 0) {
                    blockGainFocusTime = currTime + lockDuration
                }

                diff
            }

            if (dif <= 0) {
                break
            }

            Thread.sleep(dif)
        }

        CanvasInput.focusLost()
    }
}
