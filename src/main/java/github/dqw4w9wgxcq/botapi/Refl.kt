package github.dqw4w9wgxcq.botapi

import github.dqw4w9wgxcq.botapi.commons.FatalException
import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.loader.RuneliteContext
import java.lang.reflect.Field
import java.lang.reflect.Method

@Suppress("DEPRECATION")
object Refl {
    private val rsClassLoader by lazy {
        RuneliteContext.getClient()::class.java.classLoader!!
    }

    //client
    val Widget_interfaceComponents: Field
    val isLoading: Field
    val worldSelectOpen: Field
    val hasFocus: Field
    val gameState: Field
    val gameStateDecoder: Int

    //loginevent
    val Login_response0: Field
    val Login_response1: Field
    val Login_response2: Field
    val Login_response3: Field
    val banType: Field
    val banTypeDecoder: Int

    //rickkinteract
    val ViewportMouse_entityTags: Field
    val ViewportMouse_entityCount: Field
    val entityCountEncoder: Int
    val entityCountDecoder: Int
    val Scene_selectedX: Field
    val Scene_selectedY: Field
    val viewportWalking: Field

    //actor
    val pathLength: Field
    val pathLengthDecoder: Int

    init {
        fun getRsClass(name: String): Class<*> {
            return Class.forName(name, false, rsClassLoader)
        }

        val clientClass = getRsClass("client")
        isLoading = clientClass.getDeclaredField("df")
        gameState = clientClass.getDeclaredField("ci")
        gameStateDecoder = -1954783269

        val loginClass = getRsClass("bw")
        Widget_interfaceComponents = getRsClass("kn").getDeclaredField("q")
        worldSelectOpen = loginClass.getDeclaredField("cs")
        hasFocus = getRsClass("bs").getDeclaredField("au")

        Login_response0 = loginClass.getDeclaredField("bh")
        Login_response1 = loginClass.getDeclaredField("bm")
        Login_response2 = loginClass.getDeclaredField("bp")
        Login_response3 = loginClass.getDeclaredField("bx")
        banType = loginClass.getDeclaredField("ai")
        banTypeDecoder = -672921453

        val viewportMouseClass = getRsClass("hz")
        ViewportMouse_entityTags = viewportMouseClass.getDeclaredField("o")
        ViewportMouse_entityCount = viewportMouseClass.getDeclaredField("b")
        entityCountEncoder = -1806915013
        entityCountDecoder = 836673267

        val sceneClass = getRsClass("hf")
        Scene_selectedX = sceneClass.getDeclaredField("ae")
        Scene_selectedY = sceneClass.getDeclaredField("ap")
        viewportWalking = sceneClass.getDeclaredField("as")

        val actorClass = getRsClass("cl")
        pathLength = actorClass.getDeclaredField("cv")
        pathLengthDecoder = 1973809325
    }

    fun <T> Field.get2(obj: Any?): T {
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        @Suppress("UNCHECKED_CAST")
        return try {
            this.get(obj)
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        } as T
    }

    fun Field.getBoolean2(obj: Any?): Boolean {
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        return try {
            this.getBoolean(obj)
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        }
    }

    fun Field.setBoolean2(obj: Any?, value: Boolean) {
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        try {
            this.setBoolean(obj, value)
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        }
    }

    fun Field.getInt2(obj: Any?, mult: Int): Int {
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        return try {
            val raw = this.getInt(obj)
            val value = raw * mult
            value
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        }
    }

    fun Field.setInt2(obj: Any?, value: Int, mult: Int) {
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        try {
            val multedValue = value * mult
            debug { "setLong2: $value * $mult = $multedValue" }
            this.setInt(obj, multedValue)
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        }
    }

    fun Field.getLong2(obj: Any?, mult: Long): Long {
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        return try {
            val multedValue = this.getLong(obj)
            val value = multedValue / mult
            val remainder = multedValue % mult
            debug { "getLong2: $value %$multedValue" }
            if (remainder != 0L) {
                throw FatalException("mult wrong")
            }
            value
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        }
    }

    fun Field.setLong2(obj: Any?, value: Long, mult: Long) {
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        try {
            val multedValue = value * mult
            debug { "setLong2: $value*$mult=$multedValue" }
            this.setLong(obj, multedValue)
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        }
    }

    //cant use varargs, kotlin primtive auto boxing makes args different type
    fun <T> Method.invoke2(args: Any?, junk: Int): T {
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        return try {
            @Suppress("UNCHECKED_CAST")
            this.invoke(args, junk) as T
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        }
    }
}
