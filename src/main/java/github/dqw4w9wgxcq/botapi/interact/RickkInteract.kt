package github.dqw4w9wgxcq.botapi.interact

import github.dqw4w9wgxcq.botapi.Refl
import github.dqw4w9wgxcq.botapi.Refl.get2
import github.dqw4w9wgxcq.botapi.Refl.getInt2
import github.dqw4w9wgxcq.botapi.Refl.getLong2
import github.dqw4w9wgxcq.botapi.Refl.setBoolean2
import github.dqw4w9wgxcq.botapi.Refl.setInt2
import github.dqw4w9wgxcq.botapi.Refl.setLong2
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.game.Client
import github.dqw4w9wgxcq.botapi.input.mouse.Mouse
import github.dqw4w9wgxcq.botapi.input.mouse.MouseInput
import github.dqw4w9wgxcq.botapi.input.mouse.action.MouseAction
import github.dqw4w9wgxcq.botapi.loader.BotApiContext
import github.dqw4w9wgxcq.botapi.script.BotScript
import github.dqw4w9wgxcq.botapi.tabs.Tab
import github.dqw4w9wgxcq.botapi.tabs.Tabs
import github.dqw4w9wgxcq.botapi.widget.Widgets
import github.dqw4w9wgxcq.botapi.wrappers.item.container.InventoryItem
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.SceneEntity
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.actor.NPC
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.actor.Player
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.item.TileItem
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.`object`.TileObject
import github.dqw4w9wgxcq.botapi.wrappers.widget.Widget
import net.runelite.api.MenuAction
import net.runelite.api.MenuEntry
import net.runelite.api.Point
import net.runelite.api.events.MenuEntryAdded
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.eventbus.Subscribe
import java.awt.Rectangle
import java.util.concurrent.atomic.AtomicLong

class RickkInteract : InteractDriver {
    class HoveredTagManager {
        val forcedTag: AtomicLong = AtomicLong(-1L)

        @Subscribe
        fun onMenuEntryAdded(e: MenuEntryAdded) {
            if (e.menuEntry.type != MenuAction.WALK ) {
                return
            }

            if (forcedTag.get() == -1L) {
                return
            }

            debug { "setting entity tag ${forcedTag.get()}" }
            val tags: LongArray = Refl.ViewportMouse_entityTags.get2(null)
            tags[0] = forcedTag.get()
            Refl.ViewportMouse_entityCount.setInt2(null, 1, Refl.entityCountMult)
            debug { "tag after: ${Refl.ViewportMouse_entityTags.get2<LongArray>(null)[0]}" }
            debug { "entity count after: ${Refl.ViewportMouse_entityCount.getInt2(null, Refl.entityCountMult)}" }
        }
    }

    private val hoveredTagManager by lazy {
        val out = HoveredTagManager()
        BotApiContext.getEventBus().register(out)
        out
    }

    override fun cancel() {
        withDestination(
            Interact.viewportBounds,
            { it: MenuEntry -> it.type == MenuAction.CANCEL && it.option == "Cancel" }
                .withDescription("type CANCEL and option \"Cancel\"")
        )
    }

    override fun walk(scenePosition: Point) {
        debug { "walking $scenePosition" }
        onGameThread { }//make sure were at the right point in the game cycle, can fail otherwise
        wait(25)
        Refl.Scene_selectedX.setInt2(null, scenePosition.x, 1)
        Refl.Scene_selectedY.setInt2(null, scenePosition.y, 1)
        Refl.viewportWalking.setBoolean2(null, true)
    }

    override fun withInventory(invItem: InventoryItem, actionMatches: (String) -> Boolean) {
        Tabs.open(Tab.INVENTORY)
        val invIndex = invItem.index
        val widget = Widgets.get(WidgetInfo.INVENTORY).children[invIndex]
            ?: throw RetryableBotException("widgetitem at index $invIndex is null")
        val bounds = widget.bounds

        withDestination(
            bounds,
            { it: MenuEntry -> it.param0 == invIndex && actionMatches(it.option) }
                .withDescription("invItem name: ${invItem.name} index: $invIndex actionMatches:$actionMatches")
        )
    }

    override fun withEntity(target: SceneEntity, actionMatches: (String) -> Boolean) {
        val arg0: Int
        val arg1: Int
        val type: Int
        val arg2: Int
        val tag = when (target) {
            is Player -> {
                arg0 = 0
                arg1 = 0
                type = 0
                arg2 = target.index

                debug { "hash ${target.hash}" }
                calculateTag(arg0, arg1, type, false, arg2)
            }

            is NPC -> {
                arg0 = 0
                arg1 = 0
                type = 1
                arg2 = target.index

                debug { "hash ${target.hash}" }
                calculateTag(arg0, arg1, type, false, arg2)
            }

            is TileObject<*> -> {
                val sceneLocation = target.sceneLocation
                arg0 = sceneLocation.x
                arg1 = sceneLocation.y
                type = 2
                arg2 = target.id
                debug { "hash ${target.hash}" }
                calculateTag(arg0, arg1, type, false, arg2)
            }

            is TileItem -> {
                val sceneLocation = target.sceneLocation
                arg0 = sceneLocation.x
                arg1 = sceneLocation.y
                type = 3
                arg2 = target.id

                debug { "hash ${target.hash}" }
                calculateTag(arg0, arg1, type, false, arg2)
            }

            else -> {
                throw IllegalArgumentException("weird target type: " + target::class.java.simpleName)
            }
        }

        debug { "tag $tag" }

        try {
            hoveredTagManager.forcedTag.set(tag)
            withDestination(
                Interact.viewportBounds,
                { it: MenuEntry -> it.param0 == arg0 && it.param1 == arg1 && it.identifier == arg2 && actionMatches(it.option) }
                    .withDescription("target: ${target.name} actionMatches:$actionMatches")
            )
            wait(200)
        } finally {
            hoveredTagManager.forcedTag.set(-1)
        }
    }

    override fun withWidget(target: Widget, actionMatches: (String) -> Boolean) {
        val bounds = target.bounds

        //need to trim the bounds because can be clipped by parent (ge collect widget)
        val trimmedBounds = Rectangle(bounds.x, bounds.y + 1, bounds.width, bounds.height - 2)
        info { "actionMatches $actionMatches" }
        withDestination(trimmedBounds, entryMatches = { it.param1 == target.id && actionMatches(it.option) })
    }

    private fun withDestination(destination: Rectangle, entryMatches: (MenuEntry) -> Boolean) {
        if (Client.isMenuOpen) {
            debug { "menu open, async moving" }
            Mouse.asyncMove(Rectangle(-100, -100, 1000, 1000))
            waitUntil { !Client.isMenuOpen }
        }

        val successful = Mouse.actionManager.submit(InteractMouseAction(destination, entryMatches)).get()
        if (!successful) {
            throw RetryableBotException("the interaction was unsuccessful")
        }
    }

    //copy pasted out of deob
    @Suppress("SameParameterValue")
    private fun calculateTag(var0: Int, var1: Int, var2: Int, var3: Boolean, var4: Int): Long {
        var var5 =
            (var0 and 127 shl 0 or (var1 and 127 shl 7) or (var2 and 3 shl 14)).toLong() or (var4.toLong() and 4294967295L shl 17)
        if (var3) {
            var5 = var5 or 65536L
        }
        return var5
    }

    class InteractMouseAction(
        val destination: Rectangle,
        val entryMatches: (MenuEntry) -> Boolean
    ) : MouseAction(true) {
        private fun getMenuEntries(): Array<MenuEntry> {
            return Client.menuEntries.reversedArray()
        }

        override fun doAction(): Boolean {
            val startedWithMouseInDestination = if (destination.contains(Client.mouseCanvasPosition.toAwt())) {
                debug { "mouse already in destination" }
                true
            } else {
                debug { "mouse not in destination" }
                if (!MouseInput.internalMove(::interrupted, destination.randomPoint(), false, null)) {
                    debug { "interact interrupted during move" }
                    return false
                }
                wait(0, 200)
                false
            }

            if (interrupted) {
                debug { "interact action interrupted after move" }
                return false
            }

            var index: Int? = null
            waitUntil(
                250,
                5,
                {
                    index = getMenuEntries().indexOfFirst(entryMatches)
                    index != -1
                }.withDescription("entryMatches:$entryMatches")
            )

            if (interrupted) {
                debug { "interrupted after index found" }
            }

            if (startedWithMouseInDestination) {
                //when the menu is first populated, the entries are in wrong order
                waitUntilWithConfirm(50, 5) {
                    index = getMenuEntries().indexOfFirst(entryMatches)
                    index == 0
                }
            }

            if (index!! == 0) {
                MouseInput.sendClickEvents(true)
                val mousePos = Client.mouseCanvasPosition
                val radius = 25
                Mouse.asyncMove(Rectangle(mousePos.x - radius, mousePos.y - radius, radius * 2, radius * 2))
            } else {
                MouseInput.sendClickEvents(false)
                waitUntil(250, 5) { Client.isMenuOpen }

                if (interrupted) {
                    debug { "interrupted after menu open" }
                }

                val indexAfterOpen = getMenuEntries().indexOfFirst { entryMatches(it) }
                if (indexAfterOpen == -1) {
                    BotScript.nextLoopDelay = 0
                    throw RetryableBotException("no entry matched $entryMatches after menu opened")
                }

                val menuX = Client.menuX
                val menuY = Client.menuY
                val menuWidth = Client.menuWidth
                val menuHeight = Client.menuHeight
                val menuBounds = Rectangle(
                    maxOf(0, menuX - 10),
                    maxOf(0, menuY - 10),
                    minOf(menuX + menuWidth + 10, Client.canvasWidth) - menuX + 10,
                    minOf(menuY + menuHeight + 10, Client.canvasHeight) - menuY + 10
                )
                debug { "menu boundary: $menuBounds" }

                //exact dimensions
                val entryBounds = Rectangle(menuX + 1, menuY + 19 + (indexAfterOpen * 15), Client.menuWidth - 1, 15)
                debug { "entry bounds: $entryBounds" }

                MouseInput.internalMove(::interrupted, entryBounds.randomPoint(), false, menuBounds)

                if (interrupted) {
                    debug { "interuptted after move to menu" }
                    return false
                }

                val indexAfterMove = getMenuEntries().indexOfFirst { entryMatches(it) }
                if (indexAfterMove != indexAfterOpen) {
                    if (indexAfterMove == -1) {
                        BotScript.nextLoopDelay = 0
                        throw RetryableBotException("indexAfterMove is -1")
                    }

                    warn { "index after move $indexAfterMove != index $indexAfterOpen, moving again" }
                    val entryBounds2 = Rectangle(
                        menuX + 1, menuY + 19 + (indexAfterMove * 15), Client.menuWidth - 1, 15
                    )
                    MouseInput.internalMove(::interrupted, entryBounds2.randomPoint(), false, menuBounds)
                }
                if (interrupted) {
                    debug { "interuptted before click menu entry" }
                    return false
                }

                wait(0, 200)

                if (interrupted) {
                    debug { "2 interuptted before click menu entry" }
                    return false
                }

                MouseInput.sendClickEvents(true)
                val mousePos = Client.mouseCanvasPosition
                val radius = 25
                Mouse.asyncMove(Rectangle(mousePos.x - radius, mousePos.y - radius, radius * 2, radius * 2))
            }

            return true
        }
    }
}