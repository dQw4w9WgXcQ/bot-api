package github.dqw4w9wgxcq.botapi.data

import github.dqw4w9wgxcq.botapi.widget.WidgetQuery

object WidgetData {
    val enterWildernessWq = WidgetQuery(475) { it.hasAction("enter wilderness") }

    const val LOW_ALCH_INV_SLOT = 3
    const val HIGH_ALCH_INV_SLOT = 11
}