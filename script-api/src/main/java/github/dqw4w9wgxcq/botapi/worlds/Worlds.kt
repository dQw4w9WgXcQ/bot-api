package github.dqw4w9wgxcq.botapi.worlds

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.entities.Players
import github.dqw4w9wgxcq.botapi.grandexchange.GrandExchange
import github.dqw4w9wgxcq.botapi.input.mouse.Mouse
import github.dqw4w9wgxcq.botapi.itemcontainer.Bank
import github.dqw4w9wgxcq.botapi.movement.Movement
import github.dqw4w9wgxcq.botapi.script.blockingevents.LoginEvent
import github.dqw4w9wgxcq.botapi.widget.Dialog
import net.runelite.api.GameState
import net.runelite.api.World
import net.runelite.api.WorldType
import net.runelite.api.widgets.WidgetInfo

object Worlds {
    class LobbyLoadWorldsTimedOutException : RetryException("load worlds in lobby timed out")

    const val LOCATION_US = 0
    const val LOCATION_UK = 1
    const val LOCATION_AUS = 3
    const val LOCATION_GER = 7

    val ACTIVITY_DISALLOWLIST = setOf(
        "wilderness",
        "trade",
        "pvp",//pvp world, pvp arena
        "deadman",
        "skill total",
        "tournament",
        "practice",//private practice
        "unrestricted",
        "bounty",
        "beta",
        "alpha",
        " test",
        "test ",
        "high risk",
        "twisted league",
        "claim ",//claim league points
        "target",
        "house party",
        " pk",
        "pk ",
        "speedrun",
        "fresh start",
        "private world",
    )

    val TYPE_DISALLOWLIST = WorldType.values().toMutableSet().filter { it != WorldType.MEMBERS }

    val P2P: (World) -> Boolean = { it: World -> it.types.contains(WorldType.MEMBERS) }.desc("P2P")
    val F2P = P2P.negate().desc("F2P")

    private val isSuitable = { w: World ->
        ACTIVITY_DISALLOWLIST.none { w.activity.contains(it, ignoreCase = true) }
                && TYPE_DISALLOWLIST.none { w.types.any { TYPE_DISALLOWLIST.contains(it) } }
                && w.id >= 330
                && w.playerCount > 2
                && w.playerCount < 2000
                && (if (locationsAllowlist != null) locationsAllowlist!!.contains(w.location) else true)
    }.desc("isSuitable")

    var locationsAllowlist: Set<Int>? = null

    fun getCurrent(): World {
        return get(Client.world)
    }

    fun all(matches: (World) -> Boolean): List<World> {
        return Client.worldList?.filter(matches) ?: throw RetryException("worlds not loaded")
    }

    fun areWorldsLoaded(): Boolean {
        return Client.worldList != null
    }

    private fun get(id: Int): World {
        return all { it.id == id }.firstOrNull() ?: throw RetryException("no world found $id")
    }

    fun isLobbySelectorOpen(): Boolean {
        return Client.isWorldSelectorOpen
    }

    fun openLobbySelector() {
        Mouse.click(LoginEvent.getClickToSwitchBounds())

        if (!waitUntilWithConfirm(10_000, condition = { areWorldsLoaded() }.desc("worlds loaded"))) {
            throw LobbyLoadWorldsTimedOutException()
        }

        waitUntil(condition = { Client.isWorldSelectorOpen }.desc("Client.isWorldSelectorOpen"))
    }

    fun changeLobbyWorld(id: Int) {
        Client.changeWorld(get(id))
        waitUntil { Client.world == id }
    }

    private fun getBest(matches: (World) -> Boolean, selector: (World) -> Double): World {
        return all(matches).minByOrNull(selector) ?: throw RetryException("no world matched")
    }

    fun getRandom(matches: (World) -> Boolean): World {
        val all = all(matches)
        return Rand.element(all)
    }

    fun openWorldHopper() {
        if (Client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null) {
            Bank.close()
            GrandExchange.close()
            if (Dialog.isOpen()) {
                Movement.walk(Players.local().sceneLocation)
                waitUntil { !Dialog.isOpen() }
            }
            Client.openWorldHopper()
            wait(100)
            waitUntil { Client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) != null }
        }
    }

    var preHopHook = {}
    fun switchTo(world: World) {
        info { "tryna switch to world " + world.id }

        openWorldHopper()

        preHopHook()

        Client.hopToWorld(world)
        waitUntil { Client.gameState == GameState.HOPPING || Dialog.isOpen() }
        if (
            Dialog.isOpen()
            && Dialog.hasOption(
                "Yes. In future, only warn about dangerous worlds.",
                "Switch to the High Risk world."
            )
        ) {
            Dialog.chooseOption(
                "Yes. In future, only warn about dangerous worlds.",
                "Switch to the High Risk world."
            )
            waitUntil(condition = { Client.gameState == GameState.HOPPING }.desc("game state hopping"))
        }
        waitUntil { Client.gameState == GameState.HOPPING }
        waitUntil(10_000, condition = { Client.world == world.id }.desc("world id change to ${world.id}"))
        waitUntil(
            30_000,
            condition = { Client.gameState == GameState.LOGGED_IN }
                .desc("game state logged in")
        )
        waitUntil(condition = { !Client.isLoading }.desc("game state not loading"))
    }

    fun switchTo(id: Int) {
        switchTo(get(id))
    }

    fun getRandomSuitable(allowHighPop: Boolean = false, matches: (World) -> Boolean = { true }): World {
        val matches2 = if (allowHighPop) {
            matches
        } else {
            matches.and(NotHighPop())
        }
        return getRandom(matches2.and(isSuitable).and { it.id != Client.world })
    }

    fun switchToRandomSuitable(allowHighPop: Boolean = false, matches: (World) -> Boolean = { true }) {
        switchTo(getRandomSuitable(allowHighPop, matches))
    }

    class NotHighPop : (World) -> Boolean {
        val highPopF2p = all(F2P)
            .sortedByDescending { it.playerCount }
            .map { it.id }
            .take(10)
            .toSet()

        val highPopP2p = all(P2P)
            .sortedByDescending { it.playerCount }
            .map { it.id }
            .take(20)
            .toSet()

        val highPop = highPopF2p + highPopP2p
        override fun invoke(p1: World): Boolean {
            return !highPop.contains(p1.id)
        }

        override fun toString(): String {
            return "not high pop"
        }
    }

    fun onF2p(): Boolean {
        return F2P(getCurrent())
    }
}
