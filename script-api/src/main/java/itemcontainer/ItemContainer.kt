package github.dqw4w9wgxcq.botapi.itemcontainer

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.NotFoundException
import github.dqw4w9wgxcq.botapi.commons.byId
import github.dqw4w9wgxcq.botapi.commons.byName
import github.dqw4w9wgxcq.botapi.commons.onGameThread
import github.dqw4w9wgxcq.botapi.wrappers.item.container.BankItem
import github.dqw4w9wgxcq.botapi.wrappers.item.container.ContainerItem
import net.runelite.api.InventoryID
import net.runelite.api.Item
import net.runelite.api.ItemContainer
import kotlin.random.Random

abstract class ItemContainer<I : ContainerItem>(private val inventory: InventoryID) {
    protected abstract fun wrap(item: Item, index: Int): I

    open fun all(): List<I> {
        return onGameThread {
            val out: MutableList<I> = ArrayList()
            val itemContainer: ItemContainer = Client.getItemContainer(inventory) ?: return@onGameThread emptyList()
            val rlItems = itemContainer.items
            for (i in rlItems.indices) {
                val item = rlItems[i]
                if (item == null || item.id == -1) {
                    continue
                }
                val tableItem = wrap(item, i)
                if (tableItem.name == "null") {
                    continue
                }
                out.add(tableItem)
            }
            out
        }
    }

    fun all(matches: (I) -> Boolean): List<I> {
        return all().filter(matches)
    }

    fun getOrNull(matches: (I) -> Boolean): I? {//kotlin 2 dum 4 overload
        //return all(matches).maxByOrNull(selector)
        return all(matches).maxByOrNull { Random.nextDouble() }
    }

    fun getOrNull(vararg names: String): I? {
        return getOrNull(byName(*names))
    }

    fun getOrNull(vararg ids: Int): I? {
        return getOrNull(byId(*ids))
    }

    fun get(matches: (I) -> Boolean): I {
        return getOrNull(matches) ?: throw NotFoundException("No item found matching $matches in $inventory")
    }

    fun get(vararg ids: Int): I {
        return get(byId(*ids))
    }

    fun get(vararg names: String): I {
        return get(byName(*names))
    }

    //kotlin 2 dum 4 overload
//    fun get(matches: (I) -> Boolean, selector: (I) -> Double): I =
//
//    fun get(vararg ids: Int, selector: (I) -> Double): I =
//
//    fun get(vararg names: String, selector: (I) -> Double): I =
//
//    fun getOrNull(matches: (I) -> Boolean, selector: (I) -> Double): I? =
//
//    fun getOrNull(vararg names: String, selector: (I) -> Double): I? =
//
//    fun getOrNull(vararg ids: Int, selector: (I) -> Double): I? =

    fun isEmpty(): Boolean {
        return distinctCount() == 0
    }

    fun atIndex(index: Int): I {
        return atIndexOrNull(index) ?: throw NotFoundException("not found at index $index in $inventory")
    }

    fun atIndexOrNull(index: Int): I? {
        return onGameThread {
            val itemContainer: ItemContainer = Client.getItemContainer(inventory) ?: return@onGameThread null

            val items = itemContainer.items

            if (index >= items.size) return@onGameThread null

            val rlItem = items[index]
            if (rlItem == null || rlItem.id == -1) {
                return@onGameThread null
            }

            val out: I = wrap(rlItem, index)

            if (out.name == "null") return@onGameThread null

            out
        }
    }

    fun contains(matches: (I) -> Boolean): Boolean {
        return all(matches).isNotEmpty()
    }

    fun contains(vararg ids: Int): Boolean {
        return contains(byId(*ids))
    }

    fun contains(vararg names: String): Boolean {
        return contains(byName(*names))
    }

    fun first(matches: (I) -> Boolean): I {
        return firstOrNull(matches) ?: throw NotFoundException("nothing matched $matches in $inventory")
    }

    fun first(vararg ids: Int): I {
        return first(byId(*ids))
    }

    fun first(vararg names: String): I {
        return first(byName(*names))
    }

    fun firstOrNull(matches: (I) -> Boolean): I? {
        return all(matches).firstOrNull()
    }

    fun firstOrNull(vararg names: String): I? {
        return firstOrNull(byName(*names))
    }

    fun firstOrNull(vararg ids: Int): I? {
        return firstOrNull(byId(*ids))
    }

    fun distinctCount(): Int {
        return onGameThread {
            val container = Client.getItemContainer(inventory) ?: return@onGameThread 0
            container.items.count { it.id != -1 }
        }
    }

    fun count(matches: (I) -> Boolean, includeStacked: Boolean = true): Int {
        var out = 0
        for (item in all(matches)) {
            if (includeStacked) {
                out += item.quantity
            } else {
                out++
            }
        }
        return out
    }

    fun count(id: Int): Int {
        var out = 0
        var checkStackable = true
        for (item in all(byId(id))) {
            if (checkStackable && (item.isStackable || item is BankItem)) return item.quantity

            checkStackable = false
            out++
        }

        return out
    }

    fun count(vararg ids: Int, includeStacked: Boolean = true): Int {
        return count(byId(*ids), includeStacked)
    }

    fun count(vararg names: String, includeStacked: Boolean = true): Int {
        return count(byName(*names), includeStacked)
    }

    fun containsAll(vararg ids: Int): Boolean {
        val idSet = HashSet<Int>()
        for (id in ids) {
            idSet.add(id)
        }
        return containsAll(idSet)
    }

    fun containsAll(ids: Collection<Int>): Boolean {
        val mutable = ids.toMutableList()
        for (i in all { i: I -> ids.contains(i.id) }) {
            mutable.remove(i.id)
        }
        return mutable.isEmpty()
    }
}
