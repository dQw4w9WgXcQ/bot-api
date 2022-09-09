package github.dqw4w9wgxcq.botapi.tabs.prayer

import github.dqw4w9wgxcq.botapi.Skills.boostedLevel
import github.dqw4w9wgxcq.botapi.varps.Varps
import github.dqw4w9wgxcq.botapi.widget.Widgets
import net.runelite.api.Skill

object Prayers {
    fun isEnabled(prayer: Prayer): Boolean {
        return Varps.get(prayer.rl.varbit) == 1
    }

    fun togglePrayer(prayer: Prayer, on: Boolean) {
        Widgets.getOrNull(541, prayer.childId)
            ?.interact { it.equals(if (on) "activate" else "deactivate", ignoreCase = true) }
    }

    val points: Int
        get() = boostedLevel(Skill.PRAYER)
    val isQuickPrayerActive: Boolean
        get() = Varps.get(4103) == 1

    fun toggleQuickPrayer(on: Boolean) {
        Widgets.getOrNull(160, 14)?.interact { it.equals(if (on) "activate" else "deactivate", ignoreCase = true) }
    }
}