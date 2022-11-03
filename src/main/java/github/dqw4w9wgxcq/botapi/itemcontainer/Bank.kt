package github.dqw4w9wgxcq.botapi.itemcontainer

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.entities.TileObjects
import github.dqw4w9wgxcq.botapi.grandexchange.GrandExchange
import github.dqw4w9wgxcq.botapi.movement.Movement
import github.dqw4w9wgxcq.botapi.movement.pathfinding.local.LocalPathfinding
import github.dqw4w9wgxcq.botapi.widget.Dialog
import github.dqw4w9wgxcq.botapi.widget.WidgetQuery
import github.dqw4w9wgxcq.botapi.widget.Widgets
import github.dqw4w9wgxcq.botapi.wrappers.Widget
import github.dqw4w9wgxcq.botapi.wrappers.entity.tile.`object`.TileObject
import github.dqw4w9wgxcq.botapi.wrappers.item.container.BankItem
import github.dqw4w9wgxcq.botapi.wrappers.item.container.ContainerItem
import github.dqw4w9wgxcq.botapi.wrappers.item.container.InventoryItem
import net.runelite.api.InventoryID
import net.runelite.api.Item
import net.runelite.api.widgets.WidgetID
import net.runelite.api.widgets.WidgetInfo

object Bank : ItemContainer<BankItem>(InventoryID.BANK) {
    class NotInBankException(matches: (BankItem) -> Boolean) : RetryableBotException("$matches")

    private enum class TransactAction(val suffix: String?) {
        ONE("1"), FIVE("5"), TEN("10"), X("X"), SAVED_X(null), ALL("All"), ALL_BUT_ONE("All-but-one");
    }

    override fun wrap(item: Item, index: Int): BankItem {
        return BankItem(item, index)
    }

    override fun all(): List<BankItem> {
        return onGameThread {
            if (!isOpen()) {
                throw RetryableBotException("bank isnt open")
            }

            super.all()
        }
    }

    fun isOpen(): Boolean {
        return onGameThread {
            val itemsWidget = Widgets.getOrNull(WidgetInfo.BANK_ITEM_CONTAINER)
            itemsWidget != null && !itemsWidget.isHidden
        }
    }

    fun close(waitFor: Boolean = true) {
        if (!isOpen()) return

        info { "closing bank" }

        Widgets.closeWithEsc()

        if (waitFor) {
            waitUntil { !isOpen() }
            wait(50)
        }
    }

    fun getTileObject(): TileObject<*>? {
        return TileObjects.closestOrNull {
            LocalPathfinding.canReach(it, ignoreEndObject = true)
                    && (it.hasAction("Bank") || (it.hasAction("Use") && it.name.contains("bank", true)))
        }
    }

    fun open(waitFor: Boolean = true) {
        if (isOpen()) return

        info { "opening bank" }
        GrandExchange.close()

        val bankObject = getTileObject() ?: throw NotFoundException("no bank tile object found")
        if (!bankObject.interact { it == "Use" || it == "Bank" }) {
            throw RetryableBotException("a door or something opening bank")
        }

        if (waitFor) {
            waitUntil { isOpen() || Movement.isMoving() }
            if (isOpen()) {
                return
            }
            waitUntil(20_000) { isOpen() || !Movement.isMoving() }
            waitUntil { isOpen() }
        }
    }

    private val depositInventoryWq = WidgetQuery(WidgetID.BANK_GROUP_ID, byAction("Deposit inventory"))
    fun depositInventory(waitFor: Boolean = true) {
        if (Inventory.isEmpty()) {
            return
        }

        info { "depositing inventory" }
        depositInventoryWq().interact("deposit inventory")

        if (waitFor) {
            waitUntil { Inventory.isEmpty() }
        }
    }

    private val depositEquipmentQuery = WidgetQuery(WidgetID.BANK_GROUP_ID, byAction("Deposit worn items"))
    fun depositEquipment(waitFor: Boolean = true) {
        if (Equipment.isEmpty()) {
            return
        }

        info { "depositing equipment" }
        depositEquipmentQuery().interact("deposit worn items")

        if (waitFor) {
            waitUntil { Equipment.isEmpty() }
        }
    }

    fun isNotedWithdrawMode(): Boolean {
        return Client.getVarbitValue(3958) == 1
    }

    private val getWithdrawAsNote = WidgetQuery(WidgetID.BANK_GROUP_ID) { it.hasAction("Note") }
    private val getWithdrawAsItem = WidgetQuery(WidgetID.BANK_GROUP_ID) { it.hasAction("Item") }
    fun setWithdrawMode(noted: Boolean, waitFor: Boolean = true) {
        if (noted) {
            getWithdrawAsNote().interact("Note")
        } else {
            getWithdrawAsItem().interact("Item")
        }

        if (waitFor) {
            waitUntil { isNotedWithdrawMode() == noted }
        }
    }

    fun isAlwaysSetPlaceholders(): Boolean {
        return Client.getVarbitValue(3755) == 1
    }

    private val alwaysSetPlaceHoldersWidget =
        WidgetQuery(WidgetID.BANK_GROUP_ID) { it.name.contains("Always set placeholders") }

    fun toggleAlwaysSetPlaceholders(enabled: Boolean, waitFor: Boolean = true) {
        if (isAlwaysSetPlaceholders() == enabled) {
            return
        }

        alwaysSetPlaceHoldersWidget().interact(if (enabled) "Enable" else "Disable")

        if (waitFor) {
            waitUntil { isAlwaysSetPlaceholders() == enabled }
        }
    }

    fun getXQuantity(): Int = Client.getVarbitValue(3960)

    fun getQuantityModeConfig(): Int = Client.getVarbitValue(6590)

    enum class QuantityMode(val actionSuffix: String?, val config: Int) {
        ONE("1", 0), X(null, 3), ALL("All", 4)
    }

    fun setQuantityMode(mode: QuantityMode, waitFor: Boolean = true) {
        if (getQuantityModeConfig() == mode.config) {
            return
        }

        val action = "Default quantity: ${if (mode == QuantityMode.X) getXQuantity() else mode.actionSuffix!!}"
        val getWidget = WidgetQuery(WidgetID.BANK_GROUP_ID, byAction(action))()
        getWidget.interact(action)
        if (waitFor) {
            waitUntil { getQuantityModeConfig() == mode.config }
            wait(2000)//it isn't set instantly by game
        }
    }

    fun withdraw(bankItem: BankItem, quantity: Int = 1, noted: Boolean = false) {
        require(quantity >= 1) { "quantity must be >= 1" }

        if (isNotedWithdrawMode() != noted) {
            setWithdrawMode(noted)
        }

        val bankItemContainerWidget = Widgets.get(WidgetInfo.BANK_ITEM_CONTAINER)

        Widgets.scrollUntilWidgetInBounds(bankItemContainerWidget.getChild(bankItem.index))

        val transactAction = when (quantity) {
            1 -> TransactAction.ONE
            5 -> TransactAction.FIVE
            10 -> TransactAction.TEN
            getXQuantity() -> TransactAction.SAVED_X
            else -> {
                if (quantity >= bankItem.quantity) {
                    TransactAction.ALL
                } else {
                    TransactAction.X
                }
            }
        }

        val action = "Withdraw-${transactAction.suffix ?: getXQuantity()}"//if null then saved x

        bankItemContainerWidget.getChild(bankItem.index).interact(action)
        if (transactAction == TransactAction.X) {
            Dialog.enterAmount(quantity)
        }
    }

    fun withdraw(matches: (BankItem) -> Boolean, quantity: Int = 1, noted: Boolean = false) {
        withdraw(firstOrNull(matches) ?: throw NotInBankException(matches), quantity, noted)
    }

    fun withdraw(id: Int, quantity: Int = 1, noted: Boolean = false) {
        withdraw(byId(id), quantity, noted)
    }

    fun withdraw(name: String, quantity: Int = 1, noted: Boolean = false) {
        withdraw(byName(name), quantity, noted)
    }

    fun withdrawAll(matches: (BankItem) -> Boolean, noted: Boolean = false) {
        for (bankItem in all(matches).asReversed()) {
            if (Inventory.isFull()) {
                info { "stopping withdraw all because inv is full" }
                return
            }

            if (matches(bankItem)) {
                withdraw(bankItem, Int.MAX_VALUE, noted)
            }
        }
    }

    fun withdrawAll(id: Int, noted: Boolean = false) {
        withdraw(id, Int.MAX_VALUE, noted)
    }

    fun withdrawAll(name: String, noted: Boolean = false) {
        withdraw(name, Int.MAX_VALUE, noted)
    }

    fun deposit(bankInvItem: InventoryItem, quantity: Int = 1) {
        require(quantity >= 1)
        val transactAction = when (quantity) {
            1 -> TransactAction.ONE
            5 -> TransactAction.FIVE
            10 -> TransactAction.TEN
            getXQuantity() -> TransactAction.SAVED_X
            else -> {
                if (quantity >= Inventory.count(bankInvItem.id)) {
                    TransactAction.ALL
                } else {
                    TransactAction.X
                }
            }
        }

        val action = "Deposit-${transactAction.suffix ?: getXQuantity()}"//if null then saved x

        getInvWidget(bankInvItem.index).interact(action)

        if (transactAction == TransactAction.X) {
            Dialog.enterAmount(quantity)
        }
    }

    fun deposit(matches: (InventoryItem) -> Boolean, quantity: Int = 1) {
        deposit(Inventory.getOrNull(matches) ?: return, quantity)
    }

    fun deposit(id: Int, quantity: Int = 1) {
        deposit(byId(id), quantity)
    }

    fun deposit(name: String, quantity: Int = 1) {
        deposit(byName(name), quantity)
    }

    fun depositAll(matches: (InventoryItem) -> Boolean) {
        val all = Inventory.all(matches)

        val seenIds = mutableSetOf<Int>()
        for (invItem in all.shuffled()) {
            if (seenIds.contains(invItem.id)) {
                continue
            }

            seenIds.add(invItem.id)

            if (matches(invItem)) {
                deposit(invItem, Int.MAX_VALUE)
            }
        }
    }

    fun depositAll(vararg ids: Int) {
        deposit(byId(*ids), Int.MAX_VALUE)
    }

    fun depositAll(vararg names: String) {
        deposit(byName(*names), Int.MAX_VALUE)
    }

    fun openSettings() {
        Widgets.get(WidgetInfo.BANK_SETTINGS_BUTTON).interact("Show menu")
    }

    fun closeSettings() {
        Widgets.get(WidgetInfo.BANK_SETTINGS_BUTTON).interact("Close menu")
    }

    fun isSettingsOpen(): Boolean {
        return Widgets.getOrNull(WidgetID.BANK_GROUP_ID, 48) != null
    }

    fun getInvWidget(index: Int): Widget {
        return waitUntilNotNull(500) {
            Widgets.getOrNull(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER)?.getChildOrNull(index)
        }
    }

    fun getInvWidget(matches: (InventoryItem) -> Boolean): Widget {
        val index = Inventory.get(matches).index
        return getInvWidget(index)
    }

    fun normalize(
        id: Int,
        quantity: Int = 1,
        noted: Boolean = false,
        waitFor: Boolean = true,
    ): Int {
        return normalize(
            if (noted) byIdIgnoreNote(id) else byId(id),
            quantity,
            noted,
            waitFor
        )
    }

    fun normalize(
        matches: (ContainerItem) -> Boolean,
        quantity: Int = 1,
        noted: Boolean = false,
        waitFor: Boolean = true,
    ): Int {
        require(quantity >= 0)
        val count = Inventory.count(matches.and { it.isNoted == noted })

        when {
            count == quantity -> {
                return 0
            }

            count < quantity -> {
                withdraw(matches, quantity - count, noted = noted)
            }

            count > quantity -> {
                deposit(matches, count - quantity)
            }
        }

        if (waitFor) {
            waitUntil(condition = { Inventory.count(matches) == quantity }.desc("matches:$matches quantity:$quantity"))
        }

        return quantity - count
    }
}
