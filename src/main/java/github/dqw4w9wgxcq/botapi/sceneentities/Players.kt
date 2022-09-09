package github.dqw4w9wgxcq.botapi.sceneentities

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.NotFoundException
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.actor.Player

object Players : Actors<Player>() {
    fun local(): Player {
        val localPlayer = Client.localPlayer ?: throw NotFoundException("Local player not found")

        if (localPlayer.name == null) {
            throw NotFoundException("local.name is null")
        }

        return Player(localPlayer)
    }

    fun hintArrowedOrNull(): Player? {
        return Player(Client.hintArrowPlayer ?: return null)
    }

    fun hintArrowed(): Player {
        return hintArrowedOrNull() ?: throw NotFoundException("no hintArrowed player found")
    }

    fun atIndex(index: Int): Player {
        val cachedPlayers = Client.cachedPlayers

        require(index in cachedPlayers.indices)

        return Player(cachedPlayers[index])
    }

    override fun allUnsafe(matches: (Player) -> Boolean): List<Player> {
        return Client.players.filter { it.name != null }.map { Player(it) }.filter(matches)
    }
}
