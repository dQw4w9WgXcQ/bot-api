package github.dqw4w9wgxcq.botapi.varps

import github.dqw4w9wgxcq.botapi.Client
import net.runelite.api.VarPlayer

object Varps {
    fun getBit(id: Int): Int {
        return Client.getVarbitValue(id)
    }

    fun get(id: Int): Int {
        return Client.getVarpValue(id)
    }

    fun get(varplayer: VarPlayer): Int {
        return Client.getVar(varplayer)
    }

    fun membershipDays(): Int = get(1780)
}
