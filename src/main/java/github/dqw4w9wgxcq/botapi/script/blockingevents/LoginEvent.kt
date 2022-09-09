package github.dqw4w9wgxcq.botapi.script.blockingevents

import github.dqw4w9wgxcq.botapi.Client
import github.dqw4w9wgxcq.botapi.Refl
import github.dqw4w9wgxcq.botapi.Refl.get2
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

class LoginEvent : BlockingEvent() {
    companion object {
        var preventLoginHook: (() -> Boolean)? = null

        private const val buttonWidth = 100
        private const val buttonHeight = 40

        private val xPadding: Int
            get() {
                return (Client.canvasWidth - 765) / 2
            }
        private val loginBoxX: Int
            get() {
                return xPadding + 202
            }
        private val loginBoxCenter: Int
            get() {
                return loginBoxX + 180
            }
        private val cancelBounds: Rectangle
            get() {
                val var4 = loginBoxX + 180 - 80
                val var23 = 321;
                val out = Rectangle(var4 - 73, var23 - 20, buttonWidth, buttonHeight)
                debug { "Cancel bounds: $out" }
                return out
            }
        private val acceptBounds: Rectangle
            get() {
                val var4 = loginBoxCenter - 80;
                val var23 = 311;
                val out = Rectangle(var4 - 73, var23 - 20, buttonWidth, buttonHeight)
                debug { "Accept bounds: $out" }
                return out
            }
        private val okBounds: Rectangle
            get() {
                val var4 = loginBoxX + 180 // L: 1955
                val var23 = 301 // L: 1956
                val out = Rectangle(var4 - 73, var23 - 20, buttonWidth, buttonHeight) // L: 1957
                debug { "Ok bounds: $out" }
                return out
            }
        private val loginResponse: String
            get() {
                val response0 = Refl.Login_response0.get2<String>(null)
                val response1 = Refl.Login_response1.get2<String>(null)
                val response2 = Refl.Login_response2.get2<String>(null)
                val response3 = Refl.Login_response3.get2<String>(null)
                return "$response0 $response1 $response2 $response3"
            }

        private var needInitialHop = true
    }

    object LoginIndex {
        const val MAIN_MENU = 0
        const val BETA_WORLD = 1
        const val ENTER_CREDENTIALS = 2
        const val INVALID_CREDENTIALS = 3
        const val AUTHENTICATOR = 4
        const val EULA = 12
        const val DISABLED_LOCKED = 14
        const val DISCONNECTED = 24
    }

    private val loginMessageBehaviors = mutableListOf<Pair<String, () -> Boolean>>()
    fun addLoginMessageBehavior(messageContainsIgnoreCase: String, behavior: () -> Boolean) {
        loginMessageBehaviors.add(messageContainsIgnoreCase to behavior)
    }

    private val loginIndexBehaviors = mutableMapOf<Int, () -> Boolean>()
    fun addLoginIndexBehavior(loginIndex: Int, behavior: () -> Boolean) {
        loginIndexBehaviors[loginIndex] = behavior
    }

    override fun checkBlocked(): Boolean {
        if (preventLoginHook != null) {
            if (!preventLoginHook!!()) {
                preventLoginHook = null
                return false
            }

            return true
        }

        var gameState = Client.gameState

        if (gameState == GameState.LOGGED_IN) {
            return false
        }

        if (gameState == GameState.STARTING || gameState == GameState.HOPPING || gameState == GameState.LOADING || gameState == GameState.CONNECTION_LOST) {
            BotScript.nextLoopDelay = 100
            return true
        }

        //if we are logging in, wait for welcome screen
        if (gameState == GameState.LOGGING_IN) {
            waitUntil(60_000) {
                gameState = Client.gameState
                gameState != GameState.LOGGING_IN && gameState != GameState.LOADING
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
            BotScript.nextLoopDelay = 500
            return true
        }

        //WelcomeEvent.needCheck = true

        val credentials = AccountManager.credentials
        if (needInitialHop) {
            if (!Worlds.areWorldsLoaded()) {
                if (!Client.loadWorlds()) {
                    return true
                }
                waitUntil { Worlds.areWorldsLoaded() && Worlds.isLobbySelectorOpen() }
            }

            val newWorldId = Worlds.getRandom { Worlds.SUITABLE(it) && Worlds.P2P(it) }.id
            if (newWorldId != Worlds.getCurrentId()) {
                Worlds.changeLobbyWorld(newWorldId)
                waitUntil { newWorldId == Worlds.getCurrentId() }
            }
            needInitialHop = false
            info { "did initial hop" }
        }

        if (Worlds.isLobbySelectorOpen()) {
            info { "closing world selector" }
            Keyboard.esc()
            waitUntil { !Worlds.isLobbySelectorOpen() }
            wait(500)
        }

        val loginResponse = loginResponse
        info { "login response $loginResponse" }
        for (behavior in loginMessageBehaviors) {
            val messagePart = behavior.first
            val doBehavior = behavior.second
            if (loginResponse.contains(messagePart, true)) {
                if (doBehavior()) {
                    return true
                }
            }
        }

        if (loginResponse.contains("wait a few min", true)) {
            wait(30_000)
        }

        if (loginResponse.contains("need a members")) {
            Worlds.changeLobbyWorld(Worlds.getRandom { Worlds.SUITABLE(it) && !Worlds.P2P(it) }.id)
        }

        if (loginResponse.contains("update")) {
            BotScript.nextLoopDelay = -1
            return true
        }

        if (loginResponse.contains("login limit", true)) {
            throw FatalException(loginResponse)
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
                Client.username = credentials.user
                Client.setPassword(credentials.password)
                Keyboard.enter()
                Keyboard.enter()
                waitUntil { Client.gameState == GameState.LOGGING_IN || Client.gameState == GameState.LOGIN_SCREEN_AUTHENTICATOR }
                if (Client.gameState == GameState.LOGGING_IN) {
                    BotScript.nextLoopDelay = 0
                    return true
                }
            }

            LoginIndex.MAIN_MENU -> {
                Keyboard.esc()
                Keyboard.enter()
            }

            LoginIndex.AUTHENTICATOR -> {
                val auth = credentials.auth ?: throw Exception("no auth")
                val code = Totp(auth, object : Clock() {
                    override fun getCurrentInterval(): Long {
                        return super.getCurrentInterval() - 1//no clue why this is necessary
                    }
                }).now()
                info { "auth $auth now:\"$code\"" }
                Client.setOtp(code)
                Keyboard.enter()
                waitUntil({ Client.gameState == GameState.LOGGING_IN }.withDescription("gameState == LOGGING_IN"))
                BotScript.nextLoopDelay = 0
                return true
            }

            LoginIndex.DISCONNECTED -> {
                info { "disconected state" }
                Mouse.click(okBounds)
                waitUntil({ Client.loginIndex != LoginIndex.DISCONNECTED }.withDescription("loginState != DISCONNECTED"))
            }

//            State.BETA_WORLD.loginIndex -> {
//                if (Worlds.SUITABLE.invoke(Worlds.getCurrent())) {
//                    Keyboard.esc()
//                } else {
//                    Worlds.changeLobbyWorld(Worlds.getRandom(Worlds.SUITABLE.and(Worlds.P2P)).id)
//                }
//            }

            LoginIndex.EULA -> {
                Mouse.click(acceptBounds)
            }

            else -> throw FatalException("no behavior for login index $loginIndex")
        }

        return true
    }
}
