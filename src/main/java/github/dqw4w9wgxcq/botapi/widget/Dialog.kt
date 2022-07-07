package github.dqw4w9wgxcq.botapi.widget

import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.input.Keyboard
import github.dqw4w9wgxcq.botapi.wrappers.widget.Widget
import net.runelite.api.widgets.WidgetID
import net.runelite.api.widgets.WidgetInfo

object Dialog {
    fun enterAmount(amount: Int) {
        waitUntil() { isEnterAmountOpen }

        val amountString = if (amount > 0 && amount % 1_000_000 == 0) {
            (amount / 1_000_000).toString() + "m"
        } else if (amount > 0 && amount % 1000 == 0) {
            (amount / 1000).toString() + "k"
        } else {
            amount.toString()
        }

        Keyboard.type(amountString, true)
    }

    val isEnterAmountOpen: Boolean
        get() {
            val enterAmount = Widgets.getOrNull(WidgetInfo.CHATBOX_FULL_INPUT)
            return enterAmount != null && !enterAmount.isHidden
        }

    val isOpen: Boolean
        get() {
            val scrollBar = Widgets.getOrNull(162, 557)
            return scrollBar == null || scrollBar.isHidden
        }

    fun continueSpace() {
        Keyboard.space()
    }

    //tutorial island
    fun canLegacyContinue(): Boolean {
        val legacyContinue = Widgets.getOrNull(WidgetInfo.CHATBOX_FULL_INPUT)
        return legacyContinue != null && !legacyContinue.isHidden
    }

    fun canContinue(): Boolean {
        return isOpen && (isNpcContinue() || isPlayerContinue() || isWeirdContinue() || isMinigameContinue() || isWeirdererContinue() || isLevelUpContinue() || isWeirderererContinue() || isWeirdererererContinue())
    }

    private val playerContinueQuery =
        WidgetQuery(WidgetID.DIALOG_PLAYER_GROUP_ID) { it.text.contains("continue", true) }

    private fun isPlayerContinue(): Boolean {
        return playerContinueQuery.getOrNull() != null
    }

    private val npcContinueQuery = WidgetQuery(WidgetID.DIALOG_NPC_GROUP_ID) { it.text.contains("continue", true) }
    private fun isNpcContinue(): Boolean {
        return npcContinueQuery.getOrNull() != null
    }

    //not an if3 https://i.imgur.com/Jxu31EX.png
    fun isWeirdContinue(): Boolean {
        val asdf = Widgets.getOrNull(WidgetInfo.DIALOG_SPRITE) ?: return false
        return asdf.childrenList.any { it.hasAction("continue") }
    }

    //found somewhere on tutotiral in start house
    fun isMinigameContinue(): Boolean {
        return Widgets.getOrNull(229, 2) != null
    }

    //https://i.imgur.com/jVMIDYD.png
    private fun isWeirdererContinue(): Boolean {
        return Widgets.getOrNull(WidgetInfo.DIALOG_SPRITE, 2) != null
    }

    private fun isWeirderererContinue(): Boolean {
        val widget = Widgets.getOrNull(11, 4)
        return widget != null && !widget.isHidden
    }

    //https://i.imgur.com/x7SG0Bv.png
    private fun isWeirdererererContinue(): Boolean {
        return Widgets.getOrNull(633, 0, 2) != null
    }

    private fun isLevelUpContinue(): Boolean {
        return Widgets.getOrNull(WidgetInfo.LEVEL_UP) != null
    }

    fun viewingChatOptions(): Boolean {
        return optionsWidgets().isNotEmpty()
    }

    private fun optionsWidgets(): List<Widget> {
        val widget = Widgets.getOrNull(WidgetID.DIALOG_OPTION_GROUP_ID, 1) ?: return emptyList()
        return widget.childrenList.filter { it.hasListener() }
    }

    fun options(): List<String> {
        return optionsWidgets().map { it.text }
    }

    fun chooseOption(index: Int) {
        val options = optionsWidgets()

        require(index < options.size) { "tryna chosoe option index:" + index + " options.size: " + options.size }

        Keyboard.type((index + 1).toString())
    }

    fun chooseOption(vararg optionContainsIgnoreCase: String) {
        val options1 = optionsWidgets().filter { it.hasListener() }
        for ((i, option) in options1.withIndex()) {
            for (partialText in optionContainsIgnoreCase) {
                if (option.text.contains(partialText, true)) {
                    chooseOption(i)
                    return
                }
            }
        }

        throw RetryableBotException(
            "no option matched(${optionContainsIgnoreCase.joinToString()}n) \n\"${
                options().joinToString(
                    ","
                )
            }\""
        )
    }

    fun hasOption(vararg optionsContainsIgnoreCase: String): Boolean {
        for (s in options()) {
            val lowerCase = s.lowercase()
            for (s1 in optionsContainsIgnoreCase) {
                if (lowerCase.contains(s1.lowercase())) return true
            }
        }
        return false
    }

    fun processDialog(vararg optionsContainsIgnoreCase: String) {
        if (!isOpen) {
            throw RetryableBotException("dialog not open")
        }

        if (viewingChatOptions()) {
            chooseOption(*optionsContainsIgnoreCase)
            return
        }

        if (canContinue()) {
            continueSpace()
            return
        }
    }
}
