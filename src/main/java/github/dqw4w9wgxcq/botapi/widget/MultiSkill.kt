package github.dqw4w9wgxcq.botapi.widget

import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.input.Keyboard

object MultiSkill {
    const val MULTISKILL_MENU_GROUP_ID = 270

    fun isOpen(): Boolean {
        return Widgets.getOrNull(MULTISKILL_MENU_GROUP_ID, 1) != null
    }

    fun isAllSelected(): Boolean {
        return !Widgets.get(MULTISKILL_MENU_GROUP_ID, 12).hasAction("All")
    }

    fun selectAll() {
        Widgets.get(MULTISKILL_MENU_GROUP_ID, 12).interact("All")
    }

    fun startMakeAll(id: Int? = null) {
        if (!isAllSelected()) {
            selectAll()
        }

        waitUntil(2000) { isAllSelected() }

        if (id == null) {
            Dialog.continueSpace()
            return
        }

        for (i in 1..8) {//start at 1 cuz need 2 type
            if (id == Widgets.get(MULTISKILL_MENU_GROUP_ID, 13 + i).childrenList.last().itemId) {
                Keyboard.type(i.digitToChar())
                return
            }
        }

        throw IllegalArgumentException("cant find option with id $id")
    }
}
