package github.dqw4w9wgxcq.botapi.account

object AccountManager {
    var supplyCredentials: () -> Credentials = {
        Credentials(
            System.getProperty("bot.acc") ?: throw IllegalStateException("no bot.acc"),
            System.getProperty("bot.pass") ?: throw IllegalStateException("no bot.pass")
        )
    }

    var credentials: Credentials
        get() = supplyCredentials()
        set(credentials) {
            supplyCredentials = { credentials }
        }
}
