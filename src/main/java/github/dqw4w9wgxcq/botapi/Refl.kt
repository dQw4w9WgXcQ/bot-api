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
    val interfaceComponents: Field
    val isLoading: Field
    val worldSelectOpen: Field
    val hasFocus: Field
    val gameState: Field
    val gameStateDecoder: Int

    //loginevent
    val loginResponse0: Field
    val loginResponse1: Field
    val loginResponse2: Field
    val loginResponse3: Field
    val banType: Field
    val banTypeDecoder: Int

    //rickkinteract
    val entityTags: Field
    val entityCount: Field
    val entityCountEncoder: Int
    val entityCountDecoder: Int
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

        val clientClass = getRsClass("client")
        isLoading = clientClass.getDeclaredField("dn")
        gameState = clientClass.getDeclaredField("cc")
        gameStateDecoder = 849546905

        val loginClass = getRsClass("bu")
        interfaceComponents = getRsClass("bt").getDeclaredField("as")
        worldSelectOpen = loginClass.getDeclaredField("cq")
        hasFocus = getRsClass("qx").getDeclaredField("aw")

        loginResponse1 = loginClass.getDeclaredField("be")
        loginResponse2 = loginClass.getDeclaredField("bf")
        loginResponse3 = loginClass.getDeclaredField("bo")
        loginResponse0 = loginClass.getDeclaredField("bh")
        banType = loginClass.getDeclaredField("ah")
        banTypeDecoder = -1123765685

        val viewportMouseClass = getRsClass("hg")
        entityTags = viewportMouseClass.getDeclaredField("n")
        entityCount = viewportMouseClass.getDeclaredField("o")
        entityCountEncoder = 709533991
        entityCountDecoder = -1096761705

        val sceneClass = getRsClass("hc")
        selectedX = sceneClass.getDeclaredField("an")
        selectedY = sceneClass.getDeclaredField("ab")
        viewportWalking = sceneClass.getDeclaredField("al")

        val actorClass = getRsClass("ct")
        pathLength = actorClass.getDeclaredField("cd")
        pathLengthDecoder = 472227045
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
