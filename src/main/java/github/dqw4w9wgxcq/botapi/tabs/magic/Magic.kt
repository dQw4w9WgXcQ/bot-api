package github.dqw4w9wgxcq.botapi.tabs.magic

import github.dqw4w9wgxcq.botapi.Skills
import github.dqw4w9wgxcq.botapi.commons.NotFoundException
import github.dqw4w9wgxcq.botapi.commons.info
import github.dqw4w9wgxcq.botapi.commons.waitUntil
import github.dqw4w9wgxcq.botapi.commons.waitUntilNotNull
import github.dqw4w9wgxcq.botapi.input.mouse.Mouse
import github.dqw4w9wgxcq.botapi.itemcontainer.Inventory
import github.dqw4w9wgxcq.botapi.movement.Movement
import github.dqw4w9wgxcq.botapi.tabs.Tab
import github.dqw4w9wgxcq.botapi.tabs.Tabs
import github.dqw4w9wgxcq.botapi.varps.Varps
import github.dqw4w9wgxcq.botapi.widget.WidgetQuery
import github.dqw4w9wgxcq.botapi.widget.Widgets
import github.dqw4w9wgxcq.botapi.wrappers.Widget
import github.dqw4w9wgxcq.botapi.wrappers.entity.Entity
import github.dqw4w9wgxcq.botapi.wrappers.item.container.InventoryItem
import net.runelite.api.Skill
import net.runelite.api.widgets.WidgetID
import net.runelite.api.widgets.WidgetInfo

object Magic {
    const val SPELLBOOK_MODERN = 0
    const val SPELLBOOK_ANCIENT = 1
    const val SPELLBOOK_LUNAR = 2
    const val SPELLBOOK_ARCEUUS = 3

    fun autocast(config: Int, widgetNameIgnoreCase: String) {
        if (isAutocasting(config)) {
            return
        }

        Tabs.open(Tab.COMBAT)

        if (Widgets.getOrNull(201, 1) == null) {
            Widgets.get(WidgetInfo.COMBAT_SPELL_BOX).interact("Choose spell")
        }

        waitUntilNotNull {
            Widgets.getOrNull(201, 1)?.childrenList?.firstOrNull { it.hasAction(widgetNameIgnoreCase) }
        }.interact(widgetNameIgnoreCase)

        waitUntil(1000) { isAutocasting(config) }
    }

    fun isAutocasting(config: Int? = null): Boolean {
        val varpValue = Varps.getBit(276)
        return if (config == null) {
            varpValue != 0
        } else {
            varpValue == config
        }
    }

    fun isSpellSelected(spell: Spell): Boolean {
        return getWidget(spell).borderType == 2
    }

    fun cast(spell: Spell) {
        info { "Casting ${spell.widgetName}" }
        Tabs.open(Tab.MAGIC)
        val level = Skills.level(Skill.MAGIC)
        require(level >= spell.level) { "magic lvl $level < $spell" }
        getWidget(spell).interact("Cast")
    }

    fun cast(spell: Spell, matches: (InventoryItem) -> Boolean) {
        cast(spell)
        waitUntil { Tabs.isOpen(Tab.INVENTORY) }
        val invItem = Inventory.get(matches)
        invItem.interact("Cast")
    }

    fun cast(spell: Spell, target: Entity, checkReachable: Boolean = true): Boolean {
        if (checkReachable && !Movement.checkDoor(target.sceneLocation, true)) {
            return false
        }

        if (!isSpellSelected(spell)) {
            cast(spell)
            waitUntil(2000) { isSpellSelected(spell) }
        }

        target.interactUnchecked("cast")
        return true
    }

    fun getWidget(spell: Spell): Widget {
        val w = WidgetQuery(WidgetID.SPELLBOOK_GROUP_ID) { it.name.contains(spell.widgetName, true) }.getOrNull()
        if (w != null) {
            return w
        }

        Tabs.open(Tab.MAGIC)
        return WidgetQuery(WidgetID.SPELLBOOK_GROUP_ID) { it.name.contains(spell.widgetName, true) }()
    }

    fun checkRestoreSpellbookFilters() {
        info { "checkRestoreSpellbookFilters" }
        //the widget with the text isnt the actual widget with the action.  will break if mouse is not hovered
        val configs = mapOf(
            6605 to "Show <col=ffffff>Combat</col> spells",
            6609 to "Show <col=ffffff>Teleport</col> spells",
            6606 to "Show <col=ffffff>Utility</col> spells",
            6607 to "Show spells you lack the Magic level to cast",
            6608 to "Show spells you lack the runes to cast",
            12137 to "Show spells you lack the requirements to cast"
        )

        if (Widgets.getOrNull(218, 195) != null && configs.all { Varps.getBit(it.key) == 0 }) {
            return
        }

        Tabs.open(Tab.MAGIC)
        var container = Widgets.getOrNull(218, 195)
        if (container == null || container.isHidden) {
            Mouse.click(Widgets.get(218, 198).bounds)
            container = waitUntilNotNull {
                val inw = Widgets.getOrNull(218, 195)
                if (inw != null && !inw.isHidden) {
                    inw
                } else {
                    null
                }
            }
        }

        for (config in configs) {
            if (Varps.getBit(config.key) != 0) {
                container.childrenList.firstOrNull { it.text == config.value }?.interact("Change filter")
                    ?: throw NotFoundException("Could not find for text [${config.value}]")
                waitUntil { Varps.getBit(config.key) == 0 }
            }
        }

        Mouse.click(Widgets.get(218, 198).bounds)
        waitUntil {
            val w = Widgets.getOrNull(218, 195)
            w == null || w.isHidden
        }
    }
}
