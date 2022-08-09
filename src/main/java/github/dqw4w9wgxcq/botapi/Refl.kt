package github.dqw4w9wgxcq.botapi

import github.dqw4w9wgxcq.botapi.Refl.getLong2
import github.dqw4w9wgxcq.botapi.Refl.setLong2
import github.dqw4w9wgxcq.botapi.commons.debug
import github.dqw4w9wgxcq.botapi.loader.BotApiContext
import java.lang.reflect.Field
import java.lang.reflect.Method

@Suppress("DEPRECATION")
object Refl {
    private val rsClassLoader by lazy {
        BotApiContext.client::class.java.classLoader!!
    }

    //client
    val Widget_interfaceComponents: Field
    val isLoading: Field
    val worldSelectOpen: Field
    val loadWorlds: Method
    val hasFocus: Field

    //loginevent
    val loginBoxX: Field
    val loginBoxXmult: Int
    val Login_response0: Field
    val Login_response1: Field
    val Login_response2: Field
    val Login_response3: Field

    //rickkinteract
    val ViewportMouse_entityTags: Field
    val ViewportMouse_entityCount: Field
    val entityCountMult: Long
    val Scene_selectedX: Field
    val Scene_selectedY: Field
    val viewportWalking: Field

    //actor
    val pathLength: Field
    val pathLengthmult: Int

    init {
        fun getRsClass(name: String): Class<*> {
            return Class.forName(name, false, rsClassLoader)
        }

        try {
            val loginClass = getRsClass("bb")

            Widget_interfaceComponents = getRsClass("ku").getDeclaredField("v")
            isLoading = getRsClass("client").getDeclaredField("cf")
            worldSelectOpen = loginClass.getDeclaredField("ci")
            loadWorlds = getRsClass("le").getDeclaredMethod("o", Integer.TYPE)
            hasFocus = getRsClass("dv").getDeclaredField("ae")

            loginBoxX = loginClass.getDeclaredField("e")
            loginBoxXmult = 1251453039
            Login_response0 = loginClass.getDeclaredField("bz")
            Login_response1 = loginClass.getDeclaredField("bs")
            Login_response2 = loginClass.getDeclaredField("bg")
            Login_response3 = loginClass.getDeclaredField("bv")

            val viewportMouseClass = getRsClass("hn")
            ViewportMouse_entityTags = viewportMouseClass.getDeclaredField("p")
            ViewportMouse_entityCount = viewportMouseClass.getDeclaredField("g")
            entityCountMult = -520328175

            val sceneClass = getRsClass("go")
            Scene_selectedX = sceneClass.getDeclaredField("ai")
            Scene_selectedY = sceneClass.getDeclaredField("ac")
            viewportWalking = sceneClass.getDeclaredField("ap")

            val actorClass = getRsClass("cs")
            pathLength = actorClass.getDeclaredField("cm")
            pathLengthmult = -1581137343
        } catch (e: ReflectiveOperationException) {
            throw Exception("reflection init failed", e)
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
            val value = this.getInt(obj) / mult
            debug { "getLong2: $value" }
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
            val value = this.getLong(obj) / mult
            debug { "getLong2: $value" }
            value
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        }
    }

    fun Field.setLong2(obj:Any?, value:Long, mult: Long){
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        try {
            val multedValue = value * mult
            debug { "setLong2: $value * $mult = $multedValue" }
            this.setLong(obj, multedValue)
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        }
    }

    //cant use varargs, kotlin primtive auto boxing makes args different type
    fun <T> Method.invoke2(obj: Any?, @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN") int: Integer): T {
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        return try {
            @Suppress("UNCHECKED_CAST")
            this.invoke(obj, int) as T
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        }
    }
}
