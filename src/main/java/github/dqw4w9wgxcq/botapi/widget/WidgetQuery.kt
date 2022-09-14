package github.dqw4w9wgxcq.botapi.widget

import github.dqw4w9wgxcq.botapi.commons.NotFoundException
import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.wrappers.Widget
import net.runelite.api.widgets.WidgetInfo

class WidgetQuery : () -> Widget {
    private val group: Int
    private var childId: Int? = null
    private var grandChildId: Int? = null
    private var childMatches: ((Widget) -> Boolean)? = null
    private var grandchildMatches: ((Widget) -> Boolean)? = null

    constructor(group: Int, childMatches: (Widget) -> Boolean) {
        this.group = group
        this.childMatches = childMatches
    }

    constructor(
        group: Int,
        childMatches: (Widget) -> Boolean,
        grandchildMatches: ((Widget) -> Boolean)
    ) {//kotlin 2 dum 4 default param
        this.group = group
        this.childMatches = childMatches
        this.grandchildMatches = grandchildMatches
    }

    constructor(
        group: Int,
        childId: Int,
        grandchildMatches: ((Widget) -> Boolean)
    ) {
        this.group = group
        this.childId = childId
        this.grandchildMatches = grandchildMatches
    }

    constructor(info: WidgetInfo, grandchildMatches: ((Widget) -> Boolean)) : this(
        info.groupId,
        info.childId,
        grandchildMatches
    )

    fun getOrNull(): Widget? {
        if (childId == null) {
            val group = Widgets.getOrNull(group)

            if (group == null) {
                debug { "group null" }
                return null
            }

            for (w in group) {
                if (w == null) {
                    continue
                }

                if (childMatches!!(w)) {
                    childId = WidgetInfo.TO_CHILD(w.id)
                    break
                }
            }

            if (childId == null) {
                debug { "cant find child matching:$childMatches" }
                return null
            }
        }

        val child = Widgets.getOrNull(group, childId!!) ?: return null

        if (grandchildMatches == null) {
            return child
        }

        if (grandChildId == null) {
            for ((i, grandchild) in child.childrenList.withIndex()) {
                if (grandchildMatches!!(grandchild)) {
                    grandChildId = i
                    return grandchild
                }
            }

            if (grandChildId == null) {
                return null
            }

            return child.getChild(grandChildId!!)
        }

        return null
    }

    override fun invoke(): Widget {
        return getOrNull() ?: throw NotFoundException(this.toString())
    }

    override fun toString(): String {
        return "WidgetQuery[groupIndex=$group, childIndex=$childId, grandChildIndex=$grandChildId], childMatches=$childMatches, grandchildMatches=$grandchildMatches"
    }
}
