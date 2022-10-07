package github.dqw4w9wgxcq.botapi.widget

import github.dqw4w9wgxcq.botapi.commons.NotFoundException
import github.dqw4w9wgxcq.botapi.commons.info
import github.dqw4w9wgxcq.botapi.wrappers.Widget
import net.runelite.api.widgets.WidgetInfo

class WidgetQuery : () -> Widget {
    private val groupIndex: Int
    private var childMatches: ((Widget) -> Boolean)? = null
    private var grandchildMatches: ((Widget) -> Boolean)?
    private var childIndex: Int?
    private var grandChildIndex: Int? = null

    constructor(groupId: Int, childMatches: (Widget) -> Boolean) {
        this.groupIndex = groupId
        this.childMatches = childMatches
        childIndex = null
        grandchildMatches = null
    }

    constructor(
        groupId: Int,
        childMatches: (Widget) -> Boolean,
        grandchildMatches: ((Widget) -> Boolean)
    ) {//kotlin 2 dum 4 default param
        this.groupIndex = groupId
        this.childMatches = childMatches
        this.grandchildMatches = grandchildMatches
        childIndex = null
    }

    constructor(groupId: Int, childId: Int, grandchildMatches: ((Widget) -> Boolean)) {
        this.groupIndex = groupId
        this.childIndex = childId
        this.grandchildMatches = grandchildMatches
    }

    constructor(widgetInfo: WidgetInfo, grandchildMatches: ((Widget) -> Boolean)) : this(
        widgetInfo.groupId,
        widgetInfo.childId,
        grandchildMatches
    )

    fun getOrNull(): Widget? {
        if (childIndex == null) {
            val group = Widgets.getOrNull(groupIndex) ?: return null
            for (widget in group) {
                if (widget == null) continue
                if (childMatches!!(widget)) {
                    childIndex = WidgetInfo.TO_CHILD(widget.id)
                    break
                }
            }

            if (childIndex == null) return null
        }

        val child = Widgets.getOrNull(groupIndex, childIndex!!) ?: return null

        if (grandchildMatches == null) {
            return child
        }

        if (grandChildIndex == null) {
            for ((i, grandchild) in child.childrenList.withIndex()) {
                if (grandchildMatches!!(grandchild)) {
                    grandChildIndex = i
                    return grandchild
                }
            }

            if (grandChildIndex == null) return null
        }

        return child.getChild(grandChildIndex!!)
    }

    override fun invoke(): Widget {
        val orNull = getOrNull()

        if (orNull == null) {
            val toString = this.toString()
            reset()
            throw NotFoundException(toString)
        }

        return orNull
    }

    private fun reset() {
        if (childMatches != null && childIndex != null) {
            info { "resetting child index" }
            childIndex = null
        }

        if (grandchildMatches != null && grandChildIndex != null) {
            info { "resetting grand child index" }
            grandChildIndex = null
        }
    }

    override fun toString(): String {
        return "WidgetQuery[groupIndex=$groupIndex, childMatches=$childMatches, grandchildMatches=$grandchildMatches, childIndex=$childIndex, grandChildIndex=$grandChildIndex]"
    }
}