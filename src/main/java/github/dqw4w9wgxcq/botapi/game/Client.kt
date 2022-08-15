package github.dqw4w9wgxcq.botapi.game

import github.dqw4w9wgxcq.botapi.Refl
import github.dqw4w9wgxcq.botapi.Refl.get2
import github.dqw4w9wgxcq.botapi.Refl.getBoolean2
import github.dqw4w9wgxcq.botapi.Refl.invoke2
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.loader.BotApiContext
import net.runelite.api.ItemComposition
import net.runelite.api.NPCComposition
import net.runelite.api.ObjectComposition
import net.runelite.api.World

object Client : net.runelite.api.Client by BotApiContext.getClient() {
    override fun runScript(vararg args: Any?) {
        onGameThread { BotApiContext.getClient().runScript(args) }
    }

    override fun getItemDefinition(id: Int): ItemComposition {
        val out = onGameThread { BotApiContext.getClient().getItemDefinition(id) }
        if (out.name == null) {
            throw IllegalArgumentException("no item found for id:$id")
        }
        return out
    }

    override fun getObjectDefinition(id: Int): ObjectComposition {
        return onGameThread { BotApiContext.getClient().getObjectDefinition(id) }
    }

    override fun getNpcDefinition(id: Int): NPCComposition {
        return onGameThread { BotApiContext.getClient().getNpcDefinition(id) }
    }

    override fun getCachedNPCs(): Array<net.runelite.api.NPC> {
        return onGameThread { BotApiContext.getClient().cachedNPCs }
    }

    override fun getCachedPlayers(): Array<net.runelite.api.Player> {
        return onGameThread { BotApiContext.getClient().cachedPlayers }
    }

    override fun hopToWorld(world: World) {
        onGameThread { BotApiContext.getClient().hopToWorld(world) }
    }

    override fun openWorldHopper() {
        onGameThread { BotApiContext.getClient().openWorldHopper() }
    }

    override fun getVarbitValue(varbitId: Int): Int {
        return onGameThread { getVarbitValue(BotApiContext.getClient().varps, varbitId) }
    }

    val widgets: Array<Array<net.runelite.api.widgets.Widget?>?>
        get() {
            return Refl.Widget_interfaceComponents.get2(null)
        }

    val isLoading: Boolean
        get() {
            return Refl.isLoading.getBoolean2(null)
        }

    val isWorldSelectorOpen: Boolean
        get() {
            return Refl.worldSelectOpen.getBoolean2(null)
        }

    fun hasFocus(): Boolean {
        return Refl.hasFocus.getBoolean2(null)
    }

    fun loadWorlds(): Boolean {
        return onGameThread {
            @Suppress("DEPRECATION")//cant use Integer#valueOf because kotlin tries to convert it back to an Int
            Refl.loadWorlds.invoke2(null, Integer(1973466779))
        }
    }

    //doesnt work
//    val viewportBounds
//        get() = Rectangle(Client.viewportXOffset, Client.viewportYOffset, Client.viewportWidth, Client.viewportHeight)
}