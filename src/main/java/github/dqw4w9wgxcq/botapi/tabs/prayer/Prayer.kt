package github.dqw4w9wgxcq.botapi.tabs.prayer

enum class Prayer(val childId: Int, val rl: net.runelite.api.Prayer) {
    SUPERHUMAN_STRENGTH(9, net.runelite.api.Prayer.SUPERHUMAN_STRENGTH),
    RAPID_HEAL(12, net.runelite.api.Prayer.RAPID_HEAL),
    PROTECT_ITEM(13, net.runelite.api.Prayer.PROTECT_ITEM),
    HAWK_EYE(25, net.runelite.api.Prayer.HAWK_EYE),
    ULTIMATE_STRENGTH(15, net.runelite.api.Prayer.ULTIMATE_STRENGTH),
    INCREDIBLE_REFLEXES(16, net.runelite.api.Prayer.INCREDIBLE_REFLEXES),
    PROTECT_FROM_MAGIC(17, net.runelite.api.Prayer.PROTECT_FROM_MAGIC),
    PROTECT_FROM_MISSILES(18, net.runelite.api.Prayer.PROTECT_FROM_MISSILES),
    PROTECT_FROM_MELEE(19, net.runelite.api.Prayer.PROTECT_FROM_MELEE),
    EAGLE_EYE(27, net.runelite.api.Prayer.EAGLE_EYE),
    MYSTIC_MIGHT(28, net.runelite.api.Prayer.MYSTIC_MIGHT),
    REDEMPTION(21, net.runelite.api.Prayer.REDEMPTION),
    SMITE(22, net.runelite.api.Prayer.SMITE),
    PIETY(30, net.runelite.api.Prayer.PIETY),
    PRESERVE(33, net.runelite.api.Prayer.PRESERVE),
    RIGOUR(31, net.runelite.api.Prayer.RIGOUR),
    AUGURY(32, net.runelite.api.Prayer.AUGURY)
    ;

}