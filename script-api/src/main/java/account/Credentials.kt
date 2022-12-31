package github.dqw4w9wgxcq.botapi.account

data class Credentials(val user: String, val password: String, val auth: String? = null) {
    companion object {
        fun parse(colonSeparated: String): Credentials {
            val split = colonSeparated.split(":").toTypedArray()

            require(split.size >= 2)

            val auth = if (split.size >= 3) {
                split[2]
            } else {
                null
            }

            return Credentials(split[0], split[1], auth)
        }
    }
}