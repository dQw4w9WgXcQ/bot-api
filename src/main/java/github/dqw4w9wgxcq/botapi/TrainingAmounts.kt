package github.dqw4w9wgxcq.botapi

import github.dqw4w9wgxcq.botapi.commons.info
import github.dqw4w9wgxcq.botapi.skill.Skills
import net.runelite.api.Skill
import kotlin.math.ceil

object TrainingAmounts {
    data class Method(val lvlReq: Int, val exp: Double, val itemAmounts: Map<Int, Int>) {
        constructor(lvlReq: Int, exp: Double, itemId: Int, amount: Int = 1) : this(lvlReq, exp, mapOf(itemId to amount))

        init {
            require(lvlReq > 0)
            require(exp > 0)
        }
    }

    fun calculate(skill: Skill, goalLvl: Int, vararg methods: Method): Map<Int, Int> {
        fun MutableMap<Int, Int>.increment(id: Int, amount: Int) {
            put(id, getOrDefault(id, 0) + amount)
        }

        return buildMap {
            val methodss = methods.sortedBy { it.lvlReq }
            var lvlStep = Skills.level(skill)
            var expStep = Skills.experience(skill).toDouble()
            for ((i, method) in methodss.withIndex()) {
                if (method.lvlReq > lvlStep) {
                    throw IllegalStateException("method.lvlReq ${method.lvlReq} > lvlStep $lvlStep, prob no doable method")
                }

                if (i != methodss.lastIndex && methodss[i + 1].lvlReq <= lvlStep) {
                    info { "method $method is too low lvl" }
                    continue
                }

                lvlStep = minOf(
                        if (i == methodss.lastIndex) goalLvl else methodss[i + 1].lvlReq,
                        goalLvl
                )

                info { "lvlStep $lvlStep" }

                val expDif = Skills.experienceForLevel(lvlStep).toDouble() - expStep
                info { "expDif $expDif" }

                val count = ceil(expDif / method.exp).toInt()
                info { "count $count" }
                for (itemAmount in method.itemAmounts) {
                    val id = itemAmount.key
                    val amount = itemAmount.value
                    val totalAmount = count * amount
                    increment(id, totalAmount)
                }

                val addExp = count.toDouble() * method.exp
                info { "addExp: $addExp" }
                expStep += addExp
                info { "exp after $expStep" }

                if (goalLvl == lvlStep) {
                    info { "goalLvl == lvlStep" }
                    break
                }
            }
        }
    }
}
