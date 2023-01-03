package github.dqw4w9wgxcq.botapi.widget

import github.dqw4w9wgxcq.botapi.commons.RetryException
import github.dqw4w9wgxcq.botapi.commons.waitUntil
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
        if (!isAllSelected()) {
            Widgets.get(MULTISKILL_MENU_GROUP_ID, 12).interact("All")
            waitUntil { isAllSelected() }
        }
    }

    fun startMakeAll(id: Int? = null) {
        selectAll()

        if (id == null) {
            Dialog.continueSpace()
            return
        }

        for (i in 1..8) {//start at 1 cuz need 2 type
            if (id == (Widgets.get(MULTISKILL_MENU_GROUP_ID, 13 + i).childrenList.lastOrNull()?.itemId
                    ?: throw RetryException("multiskill list empty"))
            ) {
                Keyboard.type(i.digitToChar())
                return
            }
        }

        throw RetryException("cant find option with id $id")
    }
}
