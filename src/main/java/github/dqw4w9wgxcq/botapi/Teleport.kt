package github.dqw4w9wgxcq.botapi

import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.itemcontainer.equipment.Equipment
import github.dqw4w9wgxcq.botapi.itemcontainer.inventory.Inventory
import github.dqw4w9wgxcq.botapi.movement.Movement
import github.dqw4w9wgxcq.botapi.sceneentities.actors.players.Players
import github.dqw4w9wgxcq.botapi.tabs.magic.Magic
import github.dqw4w9wgxcq.botapi.tabs.magic.Spell
import github.dqw4w9wgxcq.botapi.widget.Dialog
import github.dqw4w9wgxcq.botapi.widget.WidgetQuery
import github.dqw4w9wgxcq.botapi.widget.Widgets
import github.dqw4w9wgxcq.botapi.wrappers.item.container.InventoryItem
import net.runelite.api.AnimationID
import net.runelite.api.EquipmentInventorySlot
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.WidgetID

object Teleport {
    private const val TELE_ANIMATION = 714

    private fun waitTele(animation: Int) {
        waitUntil { Players.local().animation == animation }
        val startRegion = Players.local().region
        waitUntil { Players.local().animation != animation }//bc some animations are in parts
        waitUntil { Players.local().region != startRegion }
    }

    fun tab(itemId: Int) {
        Inventory.get(itemId)
            .interact { it.equals("teleport", ignoreCase = true) || it.equals("break", ignoreCase = true) }
        waitTele(4069)//4069 is the breaking tab animation that occurs before main animatoin for all tab types i think
    }

    fun spell(spell: Spell) {
        Magic.cast(spell)
        waitTele(TELE_ANIMATION)
    }

    fun equip(actionIgnoreCase: String, slot: EquipmentInventorySlot) {
        info { "equip tele to $actionIgnoreCase with slot $slot" }
        Equipment.interact(slot, actionIgnoreCase)
        waitTele(TELE_ANIMATION)
    }

    fun jewellery(matches: (InventoryItem) -> Boolean, optionContainsIgnoreCase: String) {
        Inventory.get(matches).interact("rub")
        waitUntil { Dialog.hasOption(optionContainsIgnoreCase) || Widgets.getOrNull(187, 3) != null }

        if (Dialog.isOpen) {
            Dialog.chooseOption(optionContainsIgnoreCase)
        } else {
            WidgetQuery(WidgetID.ADVENTURE_LOG_ID, 3) {
                it.text.contains(
                    optionContainsIgnoreCase,
                    true
                )
            }().interact("continue")
        }

        waitTele(TELE_ANIMATION)
    }

    private val HOME_TELE_ANIM: Collection<Int> = listOf(
        AnimationID.BOOK_HOME_TELEPORT_1,
        AnimationID.COW_HOME_TELEPORT_1,
        AnimationID.LEAGUE_HOME_TELEPORT_1
    )

    fun home() {
        val local = Players.local()
        if (local.isMoving) {
            debug { "we are moving, tryna stop" }
            Movement.walk(local.sceneLocation)
            waitUntil(5000) { !Movement.isMoving() }
        }

        Magic.cast(Spell.Modern.LUMBRIDGE_HOME_TELEPORT)
        waitUntil { HOME_TELE_ANIM.contains(Players.local().animation) }
        info { "started home tele anim" }
        val startLoc: WorldPoint = Players.local().worldLocation
        waitUntil(20_000) { Players.local().worldLocation != startLoc }
        info { "loc changed, finished home tele" }
    }
}
