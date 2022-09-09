package github.dqw4w9wgxcq.botapi.itemcontainer

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.byId
import github.dqw4w9wgxcq.botapi.commons.byName
import github.dqw4w9wgxcq.botapi.commons.wait
import github.dqw4w9wgxcq.botapi.commons.waitUntil
import github.dqw4w9wgxcq.botapi.movement.Movement
import github.dqw4w9wgxcq.botapi.wrappers.item.container.InventoryItem
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.SceneEntity
import net.runelite.api.InventoryID
import net.runelite.api.Item

object Inventory : ItemContainer<InventoryItem>(InventoryID.INVENTORY) {
    fun isItemSelected(): Boolean {
        return Client.spellSelected
    }

    fun isFull(): Boolean {
        return distinctCount() == 28
    }

    fun dropAll(matches: (InventoryItem) -> Boolean = { true }) {
        val items = all(matches).toMutableList()
        items.shuffle()
        for (inventoryItem in items) {
            inventoryItem.drop()
            wait(100, 200)
        }
    }

    fun dropAll(vararg ids: Int) {
        dropAll(byId(*ids))
    }

    fun dropAll(vararg names: String) {
        dropAll(byName(*names))
    }

    fun dropAllExcept(matches: (InventoryItem) -> Boolean) {
        dropAll { !matches.invoke(it) }
    }

    fun dropAllExcept(vararg ids: Int) {
        dropAllExcept(byId(*ids))
    }

    fun dropAllExcept(vararg names: String) {
        dropAllExcept(byName(*names))
    }

    fun useOn(matches: (InventoryItem) -> Boolean, matches2: (InventoryItem) -> Boolean) {
        val item1 = Inventory.get(matches)
        val item2 = Inventory.get(matches2)

        if (!isItemSelected()) {
            item1.interact("use")
        }

        item2.interact("use")
    }

    fun useOn(matches: (InventoryItem) -> Boolean, target: SceneEntity, checkReachable: Boolean = true): Boolean {
        val invItem = Inventory.get(matches)

        if (checkReachable && !Movement.checkDoor(target.sceneLocation, true)) return false

        if (!isItemSelected()) {
            invItem.interact("use")
            waitUntil { isItemSelected() }
        }

        target.interactUnchecked("use")
        return true
    }

    override fun wrap(item: Item, index: Int): InventoryItem {
        return InventoryItem(item, index)
    }
}
