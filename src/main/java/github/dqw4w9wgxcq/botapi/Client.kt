package github.dqw4w9wgxcq.botapi

import github.dqw4w9wgxcq.botapi.Refl.get2
import github.dqw4w9wgxcq.botapi.Refl.getBoolean2
import github.dqw4w9wgxcq.botapi.Refl.getInt2
import github.dqw4w9wgxcq.botapi.commons.onGameThread
import github.dqw4w9wgxcq.botapi.loader.RuneliteContext
import net.runelite.api.ItemComposition
import net.runelite.api.NPCComposition
import net.runelite.api.ObjectComposition
import net.runelite.api.World
import net.runelite.api.widgets.Widget

object Client : net.runelite.api.Client by RuneliteContext.getClient() {
    override fun runScript(vararg args: Any?) {
        onGameThread { RuneliteContext.getClient().runScript(args) }
    }

    override fun getItemDefinition(id: Int): ItemComposition {
        val out = onGameThread { RuneliteContext.getClient().getItemDefinition(id) }
        if (out.name == null) {
            throw IllegalArgumentException("item composition has null name, id:$id")
        }
        return out
    }

    override fun getObjectDefinition(id: Int): ObjectComposition {
        return onGameThread { RuneliteContext.getClient().getObjectDefinition(id) }
    }

    override fun getNpcDefinition(id: Int): NPCComposition {
        return onGameThread { RuneliteContext.getClient().getNpcDefinition(id) }
    }

    override fun getCachedNPCs(): Array<net.runelite.api.NPC> {
        return onGameThread { RuneliteContext.getClient().cachedNPCs }
    }

    override fun getCachedPlayers(): Array<net.runelite.api.Player> {
        return onGameThread { RuneliteContext.getClient().cachedPlayers }
    }

    override fun hopToWorld(world: World) {
        onGameThread { RuneliteContext.getClient().hopToWorld(world) }
    }

    override fun openWorldHopper() {
        onGameThread { RuneliteContext.getClient().openWorldHopper() }
    }

    override fun getVarbitValue(varbitId: Int): Int {
        return onGameThread { getVarbitValue(RuneliteContext.getClient().varps, varbitId) }
    }

    val widgets: Array<Array<Widget?>?>
        get() = Refl.Widget_interfaceComponents.get2(null)

    val isLoading: Boolean
        get() = Refl.isLoading.getBoolean2(null)

    val isWorldSelectorOpen: Boolean
        get() = Refl.worldSelectOpen.getBoolean2(null)

    val gameStateRaw: Int
        get() = Refl.gameState.getInt2(null, Refl.gameStateDecoder)

    fun hasFocus(): Boolean {
        return Refl.hasFocus.getBoolean2(null)
    }
}