package github.dqw4w9wgxcq.botapi.script.blockingevents

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.Reflect
import github.dqw4w9wgxcq.botapi.Reflect.get2
import github.dqw4w9wgxcq.botapi.Reflect.getInt2
import github.dqw4w9wgxcq.botapi.account.AccountManager
import github.dqw4w9wgxcq.botapi.commons.*
import github.dqw4w9wgxcq.botapi.input.Keyboard
import github.dqw4w9wgxcq.botapi.input.mouse.Mouse
import github.dqw4w9wgxcq.botapi.script.BotScript
import github.dqw4w9wgxcq.botapi.worlds.Worlds
import net.runelite.api.GameState
import org.jboss.aerogear.security.otp.Totp
import org.jboss.aerogear.security.otp.api.Clock
import java.awt.Rectangle
import kotlin.system.exitProcess

class LoginEvent : BlockingEvent() {
    companion object {
        var preventLoginHook: (() -> Boolean)? = null

        private const val buttonWidth = 100
        private const val buttonHeight = 40

        private fun getXPadding(): Int {
            return (Client.canvasWidth - 765) / 2
        }

        private fun getLoginBoxX(): Int {
            return getXPadding() + 202
        }

        private fun getLoginBoxCenter(): Int {
            return getLoginBoxX() + 180
        }

        private fun getCancelBounds(): Rectangle {
            val var4 = getLoginBoxX() + 180 - 80
            val var23 = 321;
            val out = Rectangle(var4 - 73, var23 - 20, buttonWidth, buttonHeight)
            debug { "Cancel bounds: $out" }
            return out
        }

        private fun getAcceptBounds(): Rectangle {
            val var4 = getLoginBoxCenter() - 80;
            val var23 = 311;
            val out = Rectangle(var4 - 73, var23 - 20, buttonWidth, buttonHeight)
            debug { "Accept bounds: $out" }
            return out
        }

        fun getClickToSwitchBounds(): Rectangle {
            val var4 = getXPadding() + 5;
            val var5 = 463 // L: 340
            val var6 = 100 // L: 341
            val var7 = 35 // L: 342
            val out = Rectangle(var4, var5, var6, var7)
            debug { "getClickToSwitchBounds: $out" }
            return out
        }

        private fun getOkBounds(): Rectangle {
            val var4 = getLoginBoxX() + 180 // L: 1955
            val var23 = 301 // L: 1956
            val out = Rectangle(var4 - 73, var23 - 20, buttonWidth, buttonHeight) // L: 1957
            debug { "Ok bounds: $out" }
            return out
        }

        fun getLoginResponse(): String {
            val response0 = Reflect.loginResponse0.get2<String>(null)
            val response1 = Reflect.loginResponse1.get2<String>(null)
            val response2 = Reflect.loginResponse2.get2<String>(null)
            val response3 = Reflect.loginResponse3.get2<String>(null)
            return listOf(response0, response1, response2, response3).joinToString(" ")
        }

        fun getBanType(): Int {
            return Reflect.banType.getInt2(null, Reflect.banTypeDecoder)
        }

        private var needInitialHop = true
    }

    object BanType {
        const val DISABLED = 0
        const val LOCKED = 1
        const val BILLING = 2
    }

    object LoginIndex {
        const val MAIN_MENU = 0
        const val BETA_WORLD = 1
        const val ENTER_CREDENTIALS = 2
        const val INVALID_CREDENTIALS = 3
        const val AUTHENTICATOR = 4
        const val EULA = 12
        const val BANNED = 14
        const val DISCONNECTED = 24
    }

    private val loginResponseBehaviors = mutableListOf<Pair<String, () -> Boolean>>()
    private val loginIndexBehaviors = mutableMapOf<Int, () -> Boolean>()

    fun addLoginResponseBehavior(responseContainsIgnoreCase: String, behavior: () -> Boolean) {
        if (loginResponseBehaviors.any { it.first == responseContainsIgnoreCase }) {
            throw FatalException("handler for '$responseContainsIgnoreCase' already registered")
        }

        loginResponseBehaviors.add(responseContainsIgnoreCase to behavior)
    }

    fun addLoginIndexBehavior(loginIndex: Int, behavior: () -> Boolean) {
        if (loginIndexBehaviors.containsKey(loginIndex)) {
            throw FatalException("handler for $loginIndex already registered")
        }

        loginIndexBehaviors[loginIndex] = behavior
    }

    override fun checkBlocked(): Boolean {
        var gameState = Client.gameState!!

        if (gameState == GameState.LOGGED_IN) {
            return false
        }

        if (gameState == GameState.STARTING || gameState == GameState.HOPPING || gameState == GameState.LOADING || gameState == GameState.CONNECTION_LOST) {
            BotScript.nextDelay = 100
            return true
        }

        if (Client.gameStateRaw == 1000) {
            info { "gameState 1000, js5error, exiting" }
            exitProcess(203)
        }

        if (preventLoginHook != null) {
            if (!preventLoginHook!!()) {
                preventLoginHook = null
                return false
            }

            return true
        }

        debug { "gameState:$gameState" }

        //if we are logging in, wait for welcome screen
        if (gameState == GameState.LOGGING_IN) {
            if (
                !waitUntilWithConfirm(100_000) {
                    gameState = Client.gameState
                    gameState != GameState.LOGGING_IN && gameState != GameState.LOADING
                }
            ) {
                warn { "logging in took too long" }
                exitProcess(204)
            }

            info { "gamestate after $gameState" }
            if (gameState == GameState.LOGGED_IN) {
                waitUntil { WelcomeEvent.isOpen() }
                return false
            }

            return true
        }

        gameState = Client.gameState
        if (gameState != GameState.LOGIN_SCREEN && gameState != GameState.LOGIN_SCREEN_AUTHENTICATOR) {
            debug { "game state $gameState" }
            BotScript.nextDelay = 500
            return true
        }

        val credentials = AccountManager.credentials

        if (!Worlds.areWorldsLoaded()) {
            try {
                Worlds.openLobbySelector()
            } catch (e: Worlds.LobbyLoadWorldsTimedOutException) {
                info { "load worlds timed out, exiting" }
                exitProcess(202)
            }
        }

        if (Worlds.isLobbySelectorOpen()) {
            info { "closing world selector" }
            Keyboard.esc()
            waitUntil { !Worlds.isLobbySelectorOpen() }
        }

        if (needInitialHop) {
            val newWorldId = Worlds.getRandomSuitable(matches = Worlds.P2P).id
            if (Client.world != newWorldId) {
                Worlds.changeLobbyWorld(newWorldId)
            }
            needInitialHop = false
            info { "did initial hop" }
        }

        val loginResponse = getLoginResponse()
        info { "login response:$loginResponse" }
        for (responseBehavior in loginResponseBehaviors) {
            val response = responseBehavior.first
            val behavior = responseBehavior.second
            if (loginResponse.contains(response, true)) {
                info { "doing behavior for response:$response" }
                if (behavior()) {
                    return true
                }
            }
        }

//        if (loginResponse.contains("oo many login attempts", true)) {
//            throw FatalException(loginResponse)
//        }
//
//        if (loginResponse.contains("login limit", true)) {
//            throw FatalException(loginResponse)
//        }

        if (loginResponse.contains("need a members")) {
            Worlds.changeLobbyWorld(Worlds.getRandomSuitable(matches = Worlds.F2P).id)
        }

        if (loginResponse.contains("update")) {
            BotScript.looping = false
            return true
        }

        if (loginResponse.contains("your account has not logged out", true)) {
            wait(30_000)
        }

        if (loginResponse.contains("connection timed out", true)) {
            if (Client.loginIndex != LoginIndex.MAIN_MENU) {
                Keyboard.esc()
                return true
            }
        }

        val loginIndex: Int = Client.loginIndex
        info { "login index $loginIndex" }

        val doBehavior = loginIndexBehaviors[loginIndex]
        if (doBehavior != null) {
            if (doBehavior()) {
                return true
            }
        }

        when (loginIndex) {
            LoginIndex.ENTER_CREDENTIALS -> {
                info { "LoginIndex.ENTER_CREDENTIALS" }
                Client.username = credentials.user
                Client.setPassword(credentials.password)
                Keyboard.enter().get()
                Keyboard.enter().get()
                waitUntil(
                    condition = { Client.gameState == GameState.LOGGING_IN || Client.gameState == GameState.LOGIN_SCREEN_AUTHENTICATOR }
                        .desc("logging in or auth screen")
                )
                BotScript.nextDelay = 100
                return true
            }

            LoginIndex.MAIN_MENU -> {
                info { "LoginIndex.MAIN_MENU" }
                Keyboard.esc()
                Keyboard.enter()
                return true
            }

            LoginIndex.AUTHENTICATOR -> {
                info { "LoginIndex.AUTHENTICATOR" }
                val auth = credentials.auth ?: throw Exception("no auth")
                val code = Totp(auth, object : Clock() {
                    override fun getCurrentInterval(): Long {
                        return super.getCurrentInterval() - 1//no clue why this is necessary
                    }
                }).now()
                info { "auth $auth now:\"$code\"" }
                Client.setOtp(code)
                Keyboard.enter()
                waitUntil(condition = { Client.gameState == GameState.LOGGING_IN }.desc("gameState == LOGGING_IN"))
                BotScript.nextDelay = 100
                return true
            }

            LoginIndex.DISCONNECTED -> {
                info { "LoginIndex.DISCONNECTED" }
                Mouse.click(getOkBounds())
                waitUntil(condition = { Client.loginIndex != LoginIndex.DISCONNECTED }.desc("loginState != DISCONNECTED"))
                return true
            }

            LoginIndex.EULA -> {
                info { "LoginIndex.EULA" }
                Mouse.click(getAcceptBounds())
                return true
            }

            else -> throw FatalException("no behavior for login index $loginIndex")
        }
    }
}
