package github.dqw4w9wgxcq.botapi

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
    val loginBoxXDecoder: Int
    val Login_response0: Field
    val Login_response1: Field
    val Login_response2: Field
    val Login_response3: Field

    //rickkinteract
    val ViewportMouse_entityTags: Field
    val ViewportMouse_entityCount: Field
    val entityCountDecoder: Long
    val entityCountEncoder: Long
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

        try {
            val loginClass = getRsClass("bb")

            Widget_interfaceComponents = getRsClass("ku").getDeclaredField("v")
            isLoading = getRsClass("client").getDeclaredField("cf")
            worldSelectOpen = loginClass.getDeclaredField("ci")
            loadWorlds = getRsClass("le").getDeclaredMethod("o", Integer.TYPE)
            hasFocus = getRsClass("dv").getDeclaredField("ae")

            loginBoxX = loginClass.getDeclaredField("e")
            loginBoxXDecoder = 1251453039
            Login_response0 = loginClass.getDeclaredField("bz")
            Login_response1 = loginClass.getDeclaredField("bs")
            Login_response2 = loginClass.getDeclaredField("bg")
            Login_response3 = loginClass.getDeclaredField("bv")

            val viewportMouseClass = getRsClass("hn")
            ViewportMouse_entityTags = viewportMouseClass.getDeclaredField("p")
            ViewportMouse_entityCount = viewportMouseClass.getDeclaredField("g")
            entityCountDecoder = -1680997135
            entityCountEncoder = -520328175

            val sceneClass = getRsClass("go")
            Scene_selectedX = sceneClass.getDeclaredField("ai")
            Scene_selectedY = sceneClass.getDeclaredField("ac")
            viewportWalking = sceneClass.getDeclaredField("ap")

            val actorClass = getRsClass("cs")
            pathLength = actorClass.getDeclaredField("cm")
            pathLengthDecoder = -1581137343
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

    fun Field.getInt2(obj: Any?, decoder: Int): Int {
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        return try {
            this.getInt(obj) * decoder
        } finally {
            if (!wasAccessible) {
                this.isAccessible = false
            }
        }
    }

    fun Field.setInt2(obj: Any?, value: Int, encoder: Int) {
        val wasAccessible = this.isAccessible
        if (!wasAccessible) {
            this.isAccessible = true
        }

        try {
            this.setInt(obj, value * encoder)
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
