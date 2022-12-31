package github.dqw4w9wgxcq.botapi.data

object ActionData {
    private val EQUIP_ACTION_SET = setOf("Equip", "Wield", "Wear")
    val EQUIP = EQUIP_ACTION_SET::contains
}
