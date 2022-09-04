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
    val loadWorlds: Method
    val loadWorldsJunkValue: Byte
    val hasFocus: Field

    //loginevent
    val Login_response0: Field
    val Login_response1: Field
    val Login_response2: Field
    val Login_response3: Field

    //rickkinteract
    val ViewportMouse_entityTags: Field
    val ViewportMouse_entityCount: Field
    val entityCountEncodingMult: Int
    val entityCountDecodingMult: Int
    val Scene_selectedX: Field
    val Scene_selectedY: Field
    val viewportWalking: Field

    //actor
    val pathLength: Field
    val pathLengthDecodingMult: Int

    init {
        fun getRsClass(name: String): Class<*> {
            return Class.forName(name, false, rsClassLoader)
        }

        try {
            val loginClass = getRsClass("bc")
            Widget_interfaceComponents = getRsClass("md").getDeclaredField("e")
            isLoading = getRsClass("client").getDeclaredField("ck")
            worldSelectOpen = loginClass.getDeclaredField("co")
            loadWorlds = getRsClass("c").getDeclaredMethod("s", java.lang.Byte.TYPE)
            loadWorldsJunkValue = 5;
            hasFocus = getRsClass("op").getDeclaredField("ah")

            Login_response0 = loginClass.getDeclaredField("bq")
            Login_response1 = loginClass.getDeclaredField("bn")
            Login_response2 = loginClass.getDeclaredField("bl")
            Login_response3 = loginClass.getDeclaredField("bv")

            val viewportMouseClass = getRsClass("hg")
            ViewportMouse_entityTags = viewportMouseClass.getDeclaredField("l")
            ViewportMouse_entityCount = viewportMouseClass.getDeclaredField("u")
            entityCountEncodingMult = 609326827
            entityCountDecodingMult = 834575933

            val sceneClass = getRsClass("gy")
            Scene_selectedX = sceneClass.getDeclaredField("as")
            Scene_selectedY = sceneClass.getDeclaredField("ay")
            viewportWalking = sceneClass.getDeclaredField("ag")

            val actorClass = getRsClass("cx")
            pathLength = actorClass.getDeclaredField("ca")
            pathLengthDecodingMult = -2007282911
        } catch (e: ReflectiveOperationException) {
            throw FatalException("reflection init failed", e)
        }
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
                throw IllegalStateException("mult wrong")
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
    fun <T> Method.invoke2(obj: Any?, junk: Any): T {
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        return try {
            @Suppress("UNCHECKED_CAST")
            this.invoke(obj, junk) as T
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        }
    }
}
