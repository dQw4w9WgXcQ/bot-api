package github.dqw4w9wgxcq.botapi.tabs.settings

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.commons.NotFoundException
import github.dqw4w9wgxcq.botapi.commons.wait
import github.dqw4w9wgxcq.botapi.commons.waitUntil
import github.dqw4w9wgxcq.botapi.tabs.Tab
import github.dqw4w9wgxcq.botapi.tabs.Tabs
import github.dqw4w9wgxcq.botapi.widget.Dialog
import github.dqw4w9wgxcq.botapi.widget.WidgetQuery
import github.dqw4w9wgxcq.botapi.widget.Widgets
import net.runelite.api.VarPlayer
import net.runelite.api.widgets.WidgetID
import net.runelite.api.widgets.WidgetInfo

object Settings {
    private const val groupId = 116
    private const val tabVarbit = 9683

    private val acceptAid = WidgetQuery(groupId) { it.hasAction("Toggle Accept Aid") }
    fun checkAcceptAidOff() {
        if (Client.getVarbitValue(4180) == 0) {
            return
        }

        checkSettingsTabOpen(0)
        acceptAid().interact("Toggle Accept Aid")
        waitUntil { Client.getVarbitValue(4180) == 0 }
    }

    fun checkVolumesMuted() {
        if (
            Client.getVarpValue(VarPlayer.MUSIC_VOLUME) == 0
            && Client.getVarpValue(VarPlayer.SOUND_EFFECT_VOLUME) == 0
            && Client.getVarpValue(VarPlayer.AREA_EFFECT_VOLUME) == 0
        ) {
            return
        }

        checkSettingsTabOpen(1)
        //mute is unmute game broken
        WidgetQuery(116) { w -> w.hasAction("unmute") }().interact("unmute")
        for (w in Widgets.get(groupId)) {
            if (w == null) continue

            if (w.hasAction("unmute")) {
                w.interact("unmute")
            }
        }

        waitUntil {
            Client.getVarpValue(VarPlayer.MUSIC_VOLUME) == 0
                    && Client.getVarpValue(VarPlayer.SOUND_EFFECT_VOLUME) == 0
                    && Client.getVarpValue(VarPlayer.AREA_EFFECT_VOLUME) == 0
        }
    }

    val allSettings = WidgetQuery(116) { it.hasAction("All Settings") }
    fun openAllSettings() {
        if (isAllSettingsOpen()) {
            return
        }

        if (Dialog.isOpen() && Dialog.canContinue()) {
            Dialog.continueSpace()
            waitUntil { !Dialog.isOpen() }
        }

        Tabs.open(Tab.SETTINGS)
        allSettings.invoke().interact("All Settings")
        waitUntil { isAllSettingsOpen() }
    }

    fun isAllSettingsOpen(): Boolean {
        return Widgets.getOrNull(WidgetInfo.SETTINGS_INIT) != null
    }

    //varp and index switched for gameplay/keybinds
    enum class AllSettingsTab(val varp: Int, val actionSuffix: String) {
        ACTIVITIES(0, "Activities"),
        AUDIO(1, "Audio"),
        CHAT(2, "Chat"),
        CONTROLS(3, "Controls"),
        DISPLAY(4, "Display"),
        GAMEPLAY(5, "Gameplay")
    }

    fun getAllSettingsTab(): Int {
        return Client.getVarbitValue(9656)
    }

    fun openAllSettingsTab(tab: AllSettingsTab) {
        openAllSettings()

        if (getAllSettingsTab() != tab.varp) {
            WidgetQuery(WidgetID.SETTINGS_GROUP_ID, 23) { w -> w.hasAction { it.endsWith(tab.actionSuffix) } }()
                .interact { it.startsWith("Select") }
            waitUntil { getAllSettingsTab() == tab.varp }
            wait(500)
        }
    }

    private const val optionsChildIndex = 19

    enum class Option(val tab: AllSettingsTab, val scriptIndex1: Int) {
        SHIFT_DROP(AllSettingsTab.CONTROLS, 2772),
        ESC_CLOSES(AllSettingsTab.CONTROLS, 2775)
        ;
    }

    fun toggleOption(option: Option) {
        openAllSettingsTab(option.tab)
        val scrollableWidgets = Widgets.get(WidgetID.SETTINGS_GROUP_ID, optionsChildIndex)
        val toggleWidget = scrollableWidgets.childrenList
            .firstOrNull {
                it.hasAction("Toggle")
                        && it.onOpListener?.get(0) == 3847
                        && it.onOpListener?.get(1) == option.scriptIndex1
            }
            ?: throw NotFoundException("cant find option $option")
        Widgets.scrollUntilWidgetInBounds(toggleWidget)
        toggleWidget.interact("Toggle")
    }

    fun checkSettingsTabOpen(index: Int) {
        Tabs.open(Tab.SETTINGS)
        val bit = Client.getVarbitValue(tabVarbit)
        println("bit$bit")
        if (bit != index) {
            val action = when (index) {
                0 -> "Controls"
                1 -> "Audio"
                2 -> "Display"
                else -> throw IllegalArgumentException("weird index $index")
            }

            WidgetQuery(groupId) { it.hasAction(action) }.invoke().interact(action)
            waitUntil(2000) { Client.getVarbitValue(tabVarbit) == index }
        }
    }

    fun needRestoreDefaultKeybinds(): Boolean {
        val configs = mapOf(
            4675 to 1,
            4676 to 2,
            4677 to 3,
            4678 to 13,//inventory
            4679 to 4,
            4680 to 5,
            4682 to 6,
            4684 to 8,
            6517 to 9,//account settings
            4689 to 0,//logout
            4686 to 10,
            4687 to 11,
            4683 to 7,//clan
            4688 to 12
        )

        return configs.any { Client.getVarbitValue(it.key) != it.value }
    }

    fun checkRestoreDefaultKeybinds() {
        if (!needRestoreDefaultKeybinds()) {
            return
        }

        openAllSettingsTab(AllSettingsTab.CONTROLS)

        val scrollableWidgets = Widgets.get(WidgetID.SETTINGS_GROUP_ID, optionsChildIndex)
        val w = scrollableWidgets.childrenList
            .firstOrNull { it.hasAction("Select") && it.onOpListener?.get(0) == 3866 && it.onOpListener?.get(1) == 2776 }
            ?: throw NotFoundException("cant find restore default keybinds widget")
        Widgets.scrollUntilWidgetInBounds(w)
        w.interact("Select")
        waitUntil { Dialog.hasOption("yes.") }
        Dialog.chooseOption("yes.")
        waitUntil { !needRestoreDefaultKeybinds() }
    }
}
