package api.antiban

object Breaks {
    var time = System.currentTimeMillis()
        private set

    fun preventBreaking() {
        TODO()
    }

    fun onBreak(): Boolean {
        return false
    }
}