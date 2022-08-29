package github.dqw4w9wgxcq.botapi.loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

//similar to net.runelite.client.plugins.PluginClassLoader
class ScriptClassLoader extends URLClassLoader {
    private final ClassLoader parent;

    public ScriptClassLoader(File scriptJar, ClassLoader parent) throws MalformedURLException {
        // null parent classloader, or else class path scanning includes everything from the main class loader
        super(new URL[]{scriptJar.toURI().toURL()}, null);

        this.parent = parent;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return super.loadClass(name);
        } catch (ClassNotFoundException ex) {
            // fall back to main class loader
            return parent.loadClass(name);
        }
    }
}
