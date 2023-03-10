package github.dqw4w9wgxcq.botapi.itemcontainer

import github.dqw4w9wgxcq.botapi.commons.RetryException
import github.dqw4w9wgxcq.botapi.commons.byContains
import github.dqw4w9wgxcq.botapi.commons.desc
import github.dqw4w9wgxcq.botapi.widget.Dialog
import github.dqw4w9wgxcq.botapi.widget.Widgets
import github.dqw4w9wgxcq.botapi.wrappers.item.container.TradeItem
import net.runelite.api.InventoryID
import net.runelite.api.Item
import net.runelite.api.widgets.WidgetID

object Trade : ItemContainer<TradeItem>(InventoryID.TRADE) {
    private const val SECOND_SCREEN_GROUP_ID = 334
    val tradeOtherContainer = object : ItemContainer<TradeItem>(InventoryID.TRADEOTHER) {
        override fun wrap(item: Item, index: Int): TradeItem {
            return TradeItem(item, index)
        }
    }

    fun view(): View {
        val first = Widgets.getOrNull(WidgetID.PLAYER_TRADE_SCREEN_GROUP_ID, 10)
        if (first != null && !first.isHidden) {
            if (!first.hasAction("Accept")) {
                throw RetryException("it hink widg brok")
            }

            return View.FIRST
        }
        val second = Widgets.getOrNull(SECOND_SCREEN_GROUP_ID, 13)
        if (second != null && !second.isHidden) {
            if (!second.hasAction("Accept")) {
                throw RetryException("it hink widg brok")
            }

            return View.SECOND
        }
        return View.CLOSED
    }

    fun offerAll(itemId: Int) {
        require(itemId > 0)
        val tradeInv = Widgets.get(WidgetID.PLAYER_TRADE_INVENTORY_GROUP_ID, 0)
        for (item in tradeInv.childrenList) {
            if (item.itemId == itemId) {
                item.interact({ it: String -> it.startsWith("Offer-All") }.desc("offer all thing"))
                return
            }
        }
        throw RetryException("no item found")
    }

    fun offer(itemId: Int, amount: Int) {
        require(amount > 0)
        require(itemId > 0)
        val tradeInv = Widgets.get(WidgetID.PLAYER_TRADE_INVENTORY_GROUP_ID, 0)
        for (item in tradeInv.childrenList) {
            if (item.itemId == itemId) {
                val action = if (amount == 1) {
                    "offer"
                } else {
                    "offer-x"
                }
                item.interact(byContains(action))
                if (action == "offer-x") {
                    Dialog.enterAmount(amount)
                }
                return
            }
        }

        throw RetryException("no item found")
    }

    fun accept(first: Boolean) {
        Widgets.get(if (first) WidgetID.PLAYER_TRADE_SCREEN_GROUP_ID else SECOND_SCREEN_GROUP_ID, if (first) 10 else 13)
            .interact("Accept")
    }

    fun accept() {
        val view = view()
        if (view == View.CLOSED) {
            throw RetryException("trade view is closed")
        }
        accept(view == View.FIRST)
    }

    enum class View {
        CLOSED, FIRST, SECOND
    }

    override fun wrap(item: Item, index: Int): TradeItem {
        return TradeItem(item, index)
    }
}
