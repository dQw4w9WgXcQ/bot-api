package github.dqw4w9wgxcq.botapi.grandexchange

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.entities.NPCs
import github.dqw4w9wgxcq.botapi.input.Keyboard
import github.dqw4w9wgxcq.botapi.itemcontainer.Bank
import github.dqw4w9wgxcq.botapi.itemcontainer.Inventory
import github.dqw4w9wgxcq.botapi.widget.Dialog
import github.dqw4w9wgxcq.botapi.widget.WidgetQuery
import github.dqw4w9wgxcq.botapi.widget.Widgets
import github.dqw4w9wgxcq.botapi.wrappers.Widget
import github.dqw4w9wgxcq.botapi.wrappers.item.container.InventoryItem
import net.runelite.api.*
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.WidgetID
import net.runelite.api.widgets.WidgetInfo

object GrandExchange {
    enum class View {
        CLOSED, OFFERS, BUYING, SELLING, INDETERMINATE,
    }

    fun view(): View {
        val container = Widgets.getOrNull(WidgetInfo.GRAND_EXCHANGE_WINDOW_CONTAINER)
        if (container == null || container.isHidden) {
            return View.CLOSED
        }

        val offersContainer = Widgets.getOrNull(WidgetID.GRAND_EXCHANGE_GROUP_ID, 7)
        if (offersContainer != null && !offersContainer.isHidden) {
            return View.OFFERS
        }

        val offerContainer = Widgets.getOrNull(WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER)
        if (offerContainer == null) {
            debug { "offer container null" }
            return View.INDETERMINATE
        }

        return when (val text = offerContainer.getChild(18).text) {
            "Sell offer" -> {
                View.SELLING
            }

            "Buy offer" -> {
                View.BUYING
            }

            else -> {
                throw RetryException("widget broke i think weird text: $text")
            }
        }
    }

    fun currentItemId(): Int = Client.getVarpValue(VarPlayer.CURRENT_GE_ITEM)

    fun selectItem(id: Int, waitFor: Boolean = true) {
        if (id == currentItemId()) {
            debug { "item with id $id already selected" }
            return
        }

        val itemDefinition = Client.getItemDefinition(id)
        if (!itemDefinition.isTradeable) throw RetryException("not tradeable $id itemdef id:${itemDefinition.id} name:${itemDefinition.name}")
        var name = itemDefinition.name.lowercase()

        //cant use removesuffix because may b in mid of word
        val teleportIndex = name.lastIndexOf("teleport")
        if (teleportIndex != -1) {
            name = name.substring(0, teleportIndex)
        }
        val potionIndex = name.lastIndexOf("potion")
        if (potionIndex != -1) {
            name = name.substring(0, potionIndex)
        }
        val parenIndex = name.lastIndexOf("(")
        if (parenIndex != -1) {
            name = name.substring(0, parenIndex)
        }
        if (name.length > 8) {
            name = name.substring(Rand.nextInt(0, 3))
        }
        if (name.length > 13) {
            name = name.substring(0, Rand.nextInt(10, name.length))
        }
        if (name.length > 20) {
            name = name.substring(0, 20)
        }
        name = name.trim()

        val searchText = Client.getVarcStrValue(VarClientStr.INPUT_TEXT).trim()
        if (searchText.isNotEmpty()) {
            Keyboard.backspace(searchText.length)
            throw RetryException("search text not empty $$searchText")
        }

        Keyboard.type(name)

        wait(1000)
        waitUntil { Widgets.get(WidgetInfo.CHATBOX_GE_SEARCH_RESULTS).childrenList.any { it.itemId == id } }
        wait(1000)
        selectSearchResult(id, waitFor)
    }

    private fun selectSearchResult(id: Int, waitFor: Boolean = true) {
        Widgets.scrollUntilWidgetInBounds(getSearchResultWidget(id))
        getSearchResultWidget(id).interact("Select")
        if (waitFor) {
            waitUntil { currentItemId() == id }
        }
    }

    private fun getSearchResultWidget(id: Int): Widget {
        val children = Widgets.get(WidgetInfo.CHATBOX_GE_SEARCH_RESULTS).children
            ?: throw NotFoundException("ge search result has no children")
        val index = children.indexOfFirst { it != null && it.itemId == id }
        if (index == -1) throw NotFoundException("cant find result widget for id: $id")
        return children[index - 2] ?: throw NotFoundException("not in child array")
    }

    fun offerPrice(): Int {
        return Client.getVarbitValue(4398)
    }

    fun enterPrice(price: Int, waitFor: Boolean = true) {
        require(price > 0) { "price $price must be greater than 0 " }

        checkPriceWarning()

        if (offerPrice() == price) {
            debug { "price $price already selected" }
            return
        }

        if (view() == View.BUYING && price * offerQuantity() > Inventory.count(ItemID.COINS_995)) {
            throw RetryException("not enough coins")
        }

        WidgetQuery(WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER, byAction("Enter price"))().interact("Enter price")
        Dialog.enterAmount(price)
        if (waitFor) {
            waitUntil { offerPrice() == price }
        }
    }

    private val enterQuantityWq = WidgetQuery(WidgetInfo.GRAND_EXCHANGE_OFFER_CONTAINER, byAction("enter quantity"))
    fun enterQuantity(quantity: Int, waitFor: Boolean = true) {
        require(quantity > 0) { "quantity $quantity must be greater than 0" }

        checkPriceWarning()

        val offerQuantity = offerQuantity()
        if (quantity == offerQuantity) {
            debug { "quantity $quantity already selected" }
            return
        }

        when (val view = view()) {
            View.BUYING -> {
                if (offerPrice() * offerQuantity > Inventory.count(ItemID.COINS_995)) {
                    throw Exception("not enough coins2")
                }
            }

            View.SELLING -> {
                val invCount = Inventory.count(currentItemId())
                if (offerQuantity > invCount) {
                    throw Exception("offer quantity $offerQuantity > invCount $invCount")
                }
            }

            else -> {
                throw RetryException("view is $view in enterQuantity")
            }
        }

        enterQuantityWq().interact("Enter quantity")
        Dialog.enterAmount(quantity)
        if (waitFor) {
            waitUntil { quantity == offerQuantity() }
        }
    }

    fun open(waitFor: Boolean = true) {
        if (isOpen()) return

        info { "opening grand exchange" }

        Bank.close()

        NPCs.closest("Grand Exchange Clerk").interact("Exchange")

        if (waitFor) {
            waitUntil { isOpen() }
        }
    }

    fun close(waitFor: Boolean = true) {
        if (!isOpen()) return

        info { "closing grand exchange" }

        Widgets.closeWithEsc()

        if (waitFor) {
            waitUntil { !isOpen() }
            wait(50)
        }
    }

    fun offerQuantity(): Int {
        return Client.getVarbitValue(4396)
    }

    private val notNumberRegex = "\\D".toRegex()
    fun guidePrice(): Int {
        val text = Widgets.get(WidgetID.GRAND_EXCHANGE_GROUP_ID, 27).text
            ?: throw RetryException("no guide price text")
        val priceStr = text.replace(notNumberRegex, "")
        if (priceStr.isEmpty()) {
            throw RetryException("cant parse guide price, text:$text")
        }

        return priceStr.toInt()
    }

    internal fun offer(index: Int, waitFor: Boolean = true) {
        Widgets.get(WidgetInfo.GRAND_EXCHANGE_INVENTORY_ITEMS_CONTAINER, index).interact("Offer")
        if (waitFor) {
            waitUntil { view() == View.SELLING }
        }
    }

    fun offer(inventoryItem: InventoryItem, waitFor: Boolean = true) {
        offer(inventoryItem.index, waitFor)
    }

    fun offer(matches: (InventoryItem) -> Boolean, waitFor: Boolean = true) {
        offer(Inventory.get(matches), waitFor)
    }

    fun createBuyOffer(waitFor: Boolean = true) {
        checkPriceWarning()

        val group = Widgets.get(WidgetID.GRAND_EXCHANGE_GROUP_ID)
        for (i in 7..14) {
            val widget = group[i] ?: throw NotFoundException("offer widget not found at index $i")
            val w = widget.getChild(3)
            if (w.hasAction("Create <col=ff9040>Buy</col> offer")) {
                w.interact("Create <col=ff9040>Buy</col> offer")
                if (waitFor) {
                    waitUntil { view() == View.BUYING }
                }
                return
            }
        }
        throw RetryException("no free slot")
    }

    fun getOffers(): List<GrandExchangeOffer> {
        return Client.grandExchangeOffers.toList()
    }

    fun haveEmptySlot(): Boolean {
        val offers = getOffers()
        val memDays: Int = Client.getVarpValue(VarPlayer.MEMBERSHIP_DAYS)
        for (i in offers.indices) {
            if (i > 2 && memDays == 0) {
                return false
            }

            if (offers[i].state == GrandExchangeOfferState.EMPTY) {
                return true
            }
        }

        return false
    }

    fun canCollect(): Boolean {
        val completed = listOf(
            GrandExchangeOfferState.BOUGHT,
            GrandExchangeOfferState.SOLD,
            GrandExchangeOfferState.CANCELLED_BUY,
            GrandExchangeOfferState.CANCELLED_SELL
        )

        return getOffers().any { completed.contains(it.state) }
    }

    fun collect(toBank: Boolean = Inventory.count({ true }, includeStacked = false) >= 27, waitFor: Boolean = true) {
        info { "collecting toBank:$toBank" }

        checkPriceWarning()

        Widgets.get(WidgetID.GRAND_EXCHANGE_GROUP_ID, 6, 0)
            .interact(if (toBank) "Collect to bank" else "Collect to inventory")

        if (waitFor) {
            waitUntil { !canCollect() }
        }
    }

    fun isOpen(): Boolean {
        return view() != View.CLOSED
    }

    fun confirm(waitFor: Boolean = true) {
        checkPriceWarning()

        Widgets.get(WidgetID.GRAND_EXCHANGE_GROUP_ID, 29).interact("confirm")
        if (waitFor) {
            waitUntil { view() == View.OFFERS }
        }
    }

    fun checkPriceWarning() {
        val priceWarning = Widgets.getOrNull(289, 8)
        if (priceWarning != null) {
            info { "much lower price warning" }
            priceWarning.interact("yes")
            waitUntil { Widgets.getOrNull(289, 8) == null }
        }
    }

    fun abortOffer(slot: Int) {
        Widgets.get(WidgetID.GRAND_EXCHANGE_GROUP_ID, slot + 7).getChild(2).interact("abort offer")
    }
}
