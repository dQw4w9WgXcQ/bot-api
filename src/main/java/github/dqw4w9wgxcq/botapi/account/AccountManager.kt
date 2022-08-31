package github.dqw4w9wgxcq.botapi.account

import github.dqw4w9wgxcq.botapi.commons.FatalException

object AccountManager {
    var supplyCredentials: () -> Credentials = {
        Credentials(
            System.getProperty("bot.acc") ?: throw FatalException("no bot.acc"),
            System.getProperty("bot.pass") ?: throw FatalException("no bot.pass")
        )
    }

    var credentials: Credentials
        get() = supplyCredentials()
        set(credentials) {
            supplyCredentials = { credentials }
        }
}
