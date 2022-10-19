package github.dqw4w9wgxcq.botapi.worlds

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.entities.Players
import github.dqw4w9wgxcq.botapi.grandexchange.GrandExchange
import github.dqw4w9wgxcq.botapi.itemcontainer.Bank
import github.dqw4w9wgxcq.botapi.movement.Movement
import github.dqw4w9wgxcq.botapi.widget.Dialog
import net.runelite.api.GameState
import net.runelite.api.World
import net.runelite.api.WorldType
import net.runelite.api.widgets.WidgetInfo

object Worlds {
    val ACTIVITY_DISALLOW_LIST = setOf(
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
        "high risk",
        "twisted league",
        "claim ",//claim league points
        "target",
        "house party",
        " pk",
        "pk ",
        "speedrun",
        "fresh start",
        "freshstart",
        "fresh-start"
    )

    val TYPE_DISALLOW_LIST = setOf(
        WorldType.TOURNAMENT_WORLD,
        WorldType.PVP,
        WorldType.DEADMAN,
        WorldType.SEASONAL,
        WorldType.NOSAVE_MODE,
        WorldType.HIGH_RISK,
    )

    val SUITABLE = { w: World ->
        ACTIVITY_DISALLOW_LIST.none { w.activity.contains(it, ignoreCase = true) }
                && TYPE_DISALLOW_LIST.none { w.types.any { TYPE_DISALLOW_LIST.contains(it) } }
                && w.id >= 330
                && w.playerCount > 2 && w.playerCount < 2000
    }

    val P2P: (World) -> Boolean = { it.types.contains(WorldType.MEMBERS) }
    val F2P = P2P.negate()
    private val NOT_CURRENT: (World) -> Boolean = { it.id != Client.world }

    fun getCurrent(): World {
        return get(Client.world)
    }

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
        waitUntil { Client.world == id }
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
            if (Dialog.isOpen()) {
                Movement.walk(Players.local().sceneLocation)
                waitUntil { !Dialog.isOpen() }
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
            waitUntil { Client.gameState == GameState.HOPPING }
        }
        waitUntil { Client.gameState == GameState.HOPPING }
        waitUntil(10_000) { Client.world == world.id }
        waitUntil(10_000) { Client.gameState == GameState.LOGGED_IN }
        waitUntil { !Client.isLoading }
    }

    fun switchTo(id: Int) {
        switchTo(get(id))
    }

    fun switchToRandomSuitable(matches: (World) -> Boolean = { true }) {
        val highPop = all(F2P)
            .sortedByDescending { it.playerCount }
            .map { it.id }
            .take(10)
            .toMutableSet()
        highPop.addAll(
            all(P2P)
                .sortedByDescending { it.playerCount }
                .map { it.id }
                .take(20)
        )

        switchTo(getRandom(matches.and(SUITABLE).and(NOT_CURRENT).and { highPop.contains(it.id) }))
    }

    fun onF2p(): Boolean {
        return F2P(getCurrent())
    }
}