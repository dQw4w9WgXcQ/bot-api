package github.dqw4w9wgxcq.botapi

import github.dqw4w9wgxcq.botapi.loader.BotApiContext
import java.lang.reflect.Field
import java.lang.reflect.Method

@Suppress("DEPRECATION")
internal object Refl {
    private val rsClassLoader by lazy {
        BotApiContext.client::class.java.classLoader!!
    }

    //client
    val widgets: Field
    val isLoading: Field
    val isWorldSelectorOpen: Field
    val loadWorlds: Method
    val hasFocus: Field

    //loginevent
    val loginBoxX: Field
    val loginBoxXDecoder: Int
    val loginResponse1: Field
    val loginResponse2: Field
    val loginResponse3: Field

    //rickkinteract
    val entityTags: Field
    val entityCount: Field
    val entityCountDecoder: Int
    val entityCountEncoder: Int
    val selectedX: Field
    val selectedY: Field
    val viewportWalking: Field

    //actor
    val pathLength: Field
    val pathLengthDecoder: Int

    init {
        fun getRsClass(name: String): Class<*> {
            return Class.forName(name, false, rsClassLoader)
        }

        try {
            widgets = getRsClass("aq").getDeclaredField("a")
            isLoading = getRsClass("client").getDeclaredField("ch")
            isWorldSelectorOpen = getRsClass("ba").getDeclaredField("cq")
            loadWorlds = getRsClass("ed").getDeclaredMethod("c", Integer.TYPE)
            hasFocus = getRsClass("hx").getDeclaredField("az")

            val loginClass = getRsClass("ba")
            loginBoxX = loginClass.getDeclaredField("b")
            loginBoxXDecoder = -1216144849
            loginResponse1 = loginClass.getDeclaredField("be")
            loginResponse2 = loginClass.getDeclaredField("bz")
            loginResponse3 = loginClass.getDeclaredField("bp")

            val viewportMouseClass = getRsClass("he")
            entityTags = viewportMouseClass.getDeclaredField("u")
            entityCount = viewportMouseClass.getDeclaredField("r")
            entityCountDecoder = 435239719
            entityCountEncoder = 668632215

            val sceneClass = getRsClass("gg")
            selectedX = sceneClass.getDeclaredField("am")
            selectedY = sceneClass.getDeclaredField("an")
            viewportWalking = sceneClass.getDeclaredField("af")

            val actorClass = getRsClass("cj")
            pathLength = actorClass.getDeclaredField("cd")
            pathLengthDecoder = 1161369831
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
