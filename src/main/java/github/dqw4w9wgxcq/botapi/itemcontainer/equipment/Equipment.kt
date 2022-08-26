package github.dqw4w9wgxcq.botapi.itemcontainer.equipment

import github.dqw4w9wgxcq.botapi.commons.byId
import github.dqw4w9wgxcq.botapi.commons.waitUntil
import github.dqw4w9wgxcq.botapi.data.ActionData
import github.dqw4w9wgxcq.botapi.itemcontainer.ItemContainer
import github.dqw4w9wgxcq.botapi.itemcontainer.bank.Bank
import github.dqw4w9wgxcq.botapi.itemcontainer.inventory.Inventory
import github.dqw4w9wgxcq.botapi.tabs.Tab
import github.dqw4w9wgxcq.botapi.tabs.Tabs
import github.dqw4w9wgxcq.botapi.widget.Widgets
import github.dqw4w9wgxcq.botapi.wrappers.item.container.ContainerItem
import github.dqw4w9wgxcq.botapi.wrappers.item.container.EquipmentItem
import github.dqw4w9wgxcq.botapi.wrappers.widget.Widget
import net.runelite.api.EquipmentInventorySlot
import net.runelite.api.InventoryID
import net.runelite.api.widgets.WidgetID

object Equipment : ItemContainer<EquipmentItem>(InventoryID.EQUIPMENT) {
    val widgetChildIndicies = mapOf(
        EquipmentInventorySlot.HEAD to 15,
        EquipmentInventorySlot.CAPE to 16,
        EquipmentInventorySlot.AMULET to 17,
        EquipmentInventorySlot.WEAPON to 18,
        EquipmentInventorySlot.BODY to 19,
        EquipmentInventorySlot.SHIELD to 20,
        EquipmentInventorySlot.LEGS to 21,
        EquipmentInventorySlot.GLOVES to 22,
        EquipmentInventorySlot.BOOTS to 23,
        EquipmentInventorySlot.RING to 24,
        EquipmentInventorySlot.AMMO to 25,
    )

    fun equip(matches: (ContainerItem) -> Boolean, waitFor: Boolean = true) {
        //use inv count because graceful has different id
        val count = Inventory.count(matches)
        if (Bank.isOpen()) {
            Bank.getInvWidget(matches).interact(ActionData.EQUIP)
        } else {
            Inventory.get(matches).interact(ActionData.EQUIP)
        }

        if (waitFor) {
            waitUntil { Inventory.count(matches) != count }
        }
    }

    fun equip(id: Int, waitFor: Boolean = true) {
        equip(byId(id), waitFor)
    }

    fun inSlot(slot: EquipmentInventorySlot): EquipmentItem {
        return atIndex(slot.slotIdx)
    }

    fun inSlotOrNull(slot: EquipmentInventorySlot): EquipmentItem? {
        return atIndexOrNull(slot.slotIdx)
    }

    fun contains(matches: (EquipmentItem) -> Boolean, slot: EquipmentInventorySlot): Boolean {
        return matches(atIndexOrNull(slot.slotIdx) ?: return false)
    }

    fun getWidget(slot: EquipmentInventorySlot): Widget {
        return Widgets.get(WidgetID.EQUIPMENT_GROUP_ID, widgetChildIndicies[slot]!!)
    }

    fun interact(slot: EquipmentInventorySlot, actionMatches: (String) -> Boolean) {
        Tabs.open(Tab.EQUIPMENT)
        getWidget(slot).interact(actionMatches)
    }

    fun interact(slot: EquipmentInventorySlot, actionIgnoreCase: String) {
        interact(slot) { it.contains(actionIgnoreCase, true) }
    }

    fun remove(slot: EquipmentInventorySlot, waitFor: Boolean = true) {
        interact(slot, "Remove")
        if (waitFor) {
            waitUntil { inSlotOrNull(slot) == null }
        }
    }

    override fun wrap(item: net.runelite.api.Item, index: Int): EquipmentItem {
        return EquipmentItem(item, index)
    }
}