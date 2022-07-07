package github.dqw4w9wgxcq.botapi.worlds

import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.game.Client
import github.dqw4w9wgxcq.botapi.grandexchange.GrandExchange
import github.dqw4w9wgxcq.botapi.itemcontainer.bank.Bank
import github.dqw4w9wgxcq.botapi.movement.Movement
import github.dqw4w9wgxcq.botapi.sceneentities.actors.players.Players
import github.dqw4w9wgxcq.botapi.widget.Dialog
import net.runelite.api.GameState
import net.runelite.api.World
import net.runelite.api.WorldType
import net.runelite.api.widgets.WidgetInfo

object Worlds {
    val UNSUITABLE_ACTIVITY = setOf(
        "wilderness",
        "trade",
        "pvp",
        "deadman",
        "skill total",
        "tournament",
        "private practice",
        "unrestricted",
        "bounty",
        "beta",
        "high risk",
        "twisted league",
        "claim league points",
        "target",
        "house party",
    )
    val SUITABLE = { w: World ->
        UNSUITABLE_ACTIVITY.none { w.activity.contains(it, ignoreCase = true) } &&
                w.types.none {
                    it == WorldType.TOURNAMENT_WORLD
                            || it == WorldType.PVP
                            || it == WorldType.DEADMAN
                            || it == WorldType.SEASONAL
                            || it == WorldType.NOSAVE_MODE
                            || it == WorldType.HIGH_RISK
                }
                && w.id > 335
                && w.playerCount > 2 && w.playerCount < 750
    }

    val P2P: (World) -> Boolean = { it.types.contains(WorldType.MEMBERS) }
    val F2P = P2P.negate()
    private val NOT_CURRENT: (World) -> Boolean = { it.id != getCurrentId() }

    fun getCurrentId(): Int = Client.world

    fun getCurrent(): World = get(getCurrentId())

    fun all(matches: (World) -> Boolean): List<World> {
        return Client.worldList?.filter(matches) ?: throw RetryableBotException("worlds not loaded")
    }

    fun areWorldsLoaded(): Boolean {
        return Client.worldList != null
    }

    fun get(id: Int): World {
        return all { it.id == id }.firstOrNull() ?: throw RetryableBotException("no world found $id")
    }

    fun isLobbySelectorOpen(): Boolean {
        return Client.isWorldSelectorOpen
    }

    fun changeLobbyWorld(id: Int) {
        Client.changeWorld(get(id))
        waitUntil { getCurrentId() == id }
    }

    fun getBest(matches: (World) -> Boolean, selector: (World) -> Double): World {
        return all(matches).minByOrNull(selector) ?: throw RetryableBotException("no world matched")
    }

    fun getRandom(matches: (World) -> Boolean): World {
        val all = all(matches)
        return Rand.element(all)
    }

    fun openWorldHopper() {
        if (Client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null) {
            Bank.close()
            GrandExchange.close()
            if (Dialog.isOpen) {
                Movement.walk(Players.local().sceneLocation)
                waitUntil { !Dialog.isOpen }
            }
            Client.openWorldHopper()
            wait(100)
            waitUntil { Client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) != null }
        }
    }

    fun switchTo(world: World) {
        info { "tryna switch to world " + world.id }

        openWorldHopper()

        Client.hopToWorld(world)
        waitUntil { Client.gameState == GameState.HOPPING || Dialog.isOpen }
        if (Dialog.isOpen && Dialog.hasOption(
                "Yes. In future, only warn about dangerous worlds.",
                "Switch to the High Risk world."
            )
        ) {
            Dialog.chooseOption("Yes. In future, only warn about dangerous worlds.", "Switch to the High Risk world.")
            waitUntil { Client.gameState == GameState.HOPPING }
        }
        waitUntil { Client.gameState == GameState.HOPPING }
        waitUntil(10_000) { getCurrentId() == world.id }
        waitUntil(10_000) { Client.gameState == GameState.LOGGED_IN }
        waitUntil { !Client.isLoading }
    }

    fun switchTo(id: Int) {
        switchTo(get(id))
    }

    fun switchToRandomSuitable(matches: (World) -> Boolean = { true }) {
        switchTo(getRandom(matches.and(SUITABLE.and(NOT_CURRENT))))
    }

    fun onP2p(): Boolean {
        return P2P(getCurrent())
    }

    fun World.isMembers(): Boolean {
        return P2P(this)
    }
}