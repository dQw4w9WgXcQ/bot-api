package github.dqw4w9wgxcq.botapi.varps

import github.dqw4w9wgxcq.botapi.Client
import net.runelite.api.VarPlayer

object Varps {
    fun getVarPlayer(varplayer: VarPlayer): Int {
        return Client.getVarpValue(varplayer.id)
    }

    fun membershipDays(): Int = Client.getVarpValue(1780)
}
