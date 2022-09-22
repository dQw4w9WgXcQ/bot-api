package github.dqw4w9wgxcq.botapi.widget

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.NotFoundException
import github.dqw4w9wgxcq.botapi.commons.RetryableBotException
import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.commons.onGameThread
import github.dqw4w9wgxcq.botapi.input.Keyboard
import github.dqw4w9wgxcq.botapi.input.mouse.Mouse
import github.dqw4w9wgxcq.botapi.wrappers.Widget
import net.runelite.api.widgets.WidgetInfo

object Widgets {
    fun get(group: Int, child: Int): Widget {
        return getOrNull(group, child) ?: throw NotFoundException("Widget not found: g$group, c$child")
    }

    fun get(widgetInfo: WidgetInfo): Widget {
        return get(widgetInfo.groupId, widgetInfo.childId)
    }

    fun get(group: Int, child: Int, grandchild: Int): Widget {
        return getOrNull(group, child, grandchild)
            ?: throw NotFoundException("Widget not found: g$group, c$child, gc$grandchild")
    }

    fun get(info: WidgetInfo, grandchild: Int): Widget {
        return getOrNull(info, grandchild) ?: throw NotFoundException("Widget not found: info$info, gc$grandchild")
    }

    fun get(group: Int): Array<Widget?> {
        return getOrNull(group) ?: throw NotFoundException("Widget group not found: g$group")
    }

    fun getOrNull(group: Int, child: Int): Widget? {
        return Widget.wrap(Client.getWidget(group, child))
    }

    fun getOrNull(widgetInfo: WidgetInfo): Widget? {
        return getOrNull(widgetInfo.groupId, widgetInfo.childId)
    }

    fun getOrNull(info: WidgetInfo, grandchild: Int): Widget? {
        return getOrNull(info)?.getChild(grandchild)
    }

    fun getOrNull(group: Int, child: Int, grandchild: Int): Widget? {
        return getOrNull(group, child)?.getChild(grandchild)
    }

    fun getOrNull(group: Int): Array<Widget?>? {
        return Widget.wrap(Client.widgets[group])
    }

    fun scrollUntilWidgetInBounds(widget: Widget) {
        val container = onGameThread {
            val parent = widget.parent
            if (parent.hasListener()) return@onGameThread parent
            debug { "parent isnt scroller, trying grandparent" }
            val grandParent = parent.parent
            if (!grandParent.hasListener()) throw Exception("scroll height weird p:${parent.id} gp:${grandParent.id}")
            grandParent
        }

        debug { "container: ${container.id}" }

        val containerBounds = container.bounds
        val widgetBounds = widget.bounds
        if (containerBounds.contains(widgetBounds)) {
            return
        }

        val direction = widgetBounds.centerY < containerBounds.centerY
        debug { "direction $direction" }
        Mouse.scrollUntil(containerBounds, direction, 20) { containerBounds.contains(widget.bounds) }
    }

    fun escClosesInterface(): Boolean {
        return Client.getVarbitValue(4681) == 1
    }

    fun closeWithEsc() {
        if (escClosesInterface()) {
            Keyboard.esc()
        } else {
            throw RetryableBotException("esc closes interface not enabled", retries = 3)
        }
    }
}
