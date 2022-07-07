package github.dqw4w9wgxcq.botapi.loader

import org.slf4j.LoggerFactory
import java.io.File
import java.lang.reflect.Modifier
import java.net.URLClassLoader
import java.util.jar.JarFile

class ScriptManager {
    private val log = LoggerFactory.getLogger(ScriptManager::class.java)
    val scriptsDir = File(File(System.getProperty("user.home"), "runelite-bot"), "scripts")

    private val scriptThread = ScriptThread().also { it.start() }

    /**
     * @return true if new script was successfully started, false if script was already running or failed to start
     */
    fun startScript(scriptClass: Class<out IBotScript>): Boolean {
        val activeScript = scriptThread.activeScript
        if (activeScript != null) {
            log.info("script is running already")
            return false
        }

        val script: IBotScript = try {
            scriptClass.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            log.warn("script init", e)
            return false
        }

        val accepted = scriptThread.offer(script)

        if (!accepted) {
            log.warn("script $script not accepted by scriptThread")
            return false
        }

        log.info("started script" + script::class.java.simpleName)
        return true
    }

    /**
     * @return true if no script running
     */
    fun stopScript(): Boolean {
        val activeScript = scriptThread.activeScript
        return if (activeScript == null) {
            log.info("no script running")
            true
        } else {
            log.info("asking script to stop")
            activeScript.stopLooping()
            false
        }
    }

    fun loadScripts(): List<Class<out IBotScript>> {
        return buildList {
            addAll(loadScriptsFromDir(scriptsDir))

            val desktopScriptJarFile = File(System.getProperty("user.home")).resolve("Desktop").resolve("script.jar")
            if (desktopScriptJarFile.exists()) {
                addAll(loadScriptsFromFile(desktopScriptJarFile))
            } else {
                log.info("desktop script jar not found")
            }
        }
    }

    private fun loadScriptsFromDir(dir: File): List<Class<out IBotScript>> {
        return buildList {
            val files = dir.listFiles { pathname: File -> !pathname.isDirectory && pathname.name.endsWith(".jar") }
            if (files == null) {
                log.warn("no script directory")
                return emptyList()
            }

            for (file in files) {
                addAll(loadScriptsFromFile(file))
            }
        }
    }

    private fun loadScriptsFromFile(file: File): List<Class<out IBotScript>> {
        val out: MutableList<Class<out IBotScript>> = ArrayList()
        val startTime = System.currentTimeMillis()

        JarFile(file).use { jar ->
            URLClassLoader(arrayOf(file.toURI().toURL())).use { ucl ->
                val elems = jar.entries()
                while (elems.hasMoreElements()) {
                    val entry = elems.nextElement()
                    var name = entry.name
                    if (name.contains("module-info")) continue//idk lol
                    if (name.endsWith(".class")) {
                        name = name.substring(0, name.length - ".class".length)
                        name = name.replace('/', '.')
                        val clazz = ucl.loadClass(name)
                        if (clazz.getAnnotation(ScriptMeta::class.java) != null && IBotScript::class.java.isAssignableFrom(
                                clazz
                            ) && !Modifier.isAbstract(clazz.modifiers)
                        ) {
                            @Suppress("UNCHECKED_CAST") out.add(clazz as Class<out IBotScript>)
                        }
                    }
                }
            }
        }

        log.info("loaded scripts[${out.joinToString { it.getAnnotation(ScriptMeta::class.java).value }}] from file: $file in ${System.currentTimeMillis() - startTime}ms")

        return out
    }
}
