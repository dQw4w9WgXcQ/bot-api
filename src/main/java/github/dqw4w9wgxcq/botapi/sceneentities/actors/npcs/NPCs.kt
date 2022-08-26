package github.dqw4w9wgxcq.botapi.sceneentities.actors.npcs

import github.dqw4w9wgxcq.botapi.commons.NotFoundException
import github.dqw4w9wgxcq.botapi.game.Client
import github.dqw4w9wgxcq.botapi.sceneentities.actors.Actors
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.actor.NPC

object NPCs : Actors<NPC>() {
    fun atIndex(index: Int): NPC {
        require(index in 0..32767) {}

        return NPC(Client.cachedNPCs[index])
    }

    fun hintArrowedOrNull(): NPC? {
        return NPC(Client.hintArrowNpc ?: return null)
    }

    fun hintArrowed(): NPC {
        return hintArrowedOrNull() ?: throw NotFoundException("no hint arrowed npc found")
    }

    override fun allUnsafe(matches: (NPC) -> Boolean): List<NPC> {
        return Client.npcs.filter { !it.name.isNullOrEmpty() }.map { NPC(it) }.filter(matches)
    }
}
