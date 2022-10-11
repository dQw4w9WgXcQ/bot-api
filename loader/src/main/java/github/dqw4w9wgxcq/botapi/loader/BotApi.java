package github.dqw4w9wgxcq.botapi.loader;

import com.google.inject.Injector;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.config.WarningOnExit;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.antidrag.AntiDragPlugin;
import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.plugins.devtools.DevToolsPlugin;
import net.runelite.client.plugins.fps.FpsPlugin;
import net.runelite.client.plugins.hiscore.HiscorePlugin;
import net.runelite.client.plugins.info.InfoPlugin;
import net.runelite.client.plugins.lowmemory.LowMemoryPlugin;
import net.runelite.client.plugins.menuentryswapper.MenuEntrySwapperPlugin;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class BotApi {
    public static final File DIR = new File(RealUserHome.getUserHome(), "runelite-bot");

    @Data
    private static class ManagedConfig<T> {
        private final String groupName;
        private final String key;
        private final Class<T> type;
        private final T config;
    }

    private static JFrame frame = null;
    private static ScriptManager scriptManager;

    private static final Set<Class<? extends Plugin>> enabledPlugins = new HashSet<>(Arrays.asList(
            ConfigPlugin.class,
            FpsPlugin.class,
            MenuEntrySwapperPlugin.class,
            AntiDragPlugin.class,
            HiscorePlugin.class,
            InfoPlugin.class,
            XpTrackerPlugin.class,
            LowMemoryPlugin.class
    ));

    private static final List<ManagedConfig<?>> managedConfigs = Arrays.asList(
            new ManagedConfig<>(RuneLiteConfig.GROUP_NAME, "rememberScreenBounds", Boolean.class, false),
            new ManagedConfig<>(RuneLiteConfig.GROUP_NAME, "uiEnableCustomChrome", Boolean.class, false),//needs restart
            new ManagedConfig<>(RuneLiteConfig.GROUP_NAME, "warningOnExit", WarningOnExit.class, WarningOnExit.NEVER),
            new ManagedConfig<>(RuneLiteConfig.GROUP_NAME, "trayIcon", Boolean.class, false),//needs restart
            new ManagedConfig<>(RuneLiteConfig.GROUP_NAME, "notificationTray", Boolean.class, false),

            new ManagedConfig<>("fpscontrol", "limitFps", Boolean.class, true),
            new ManagedConfig<>("fpscontrol", "maxFps", Integer.class, 30),

            new ManagedConfig<>("antiDrag", "onShiftOnly", Boolean.class, false)
    );

    @SneakyThrows
    public static void init(ClassLoader rlLoader) {
        String acc = System.getProperty("bot.acc");
        String proxy = System.getProperty("socksProxyHost");

        scriptManager = new ScriptManager(RuneLite.class.getClassLoader());

        SwingUtilities.invokeAndWait(() -> {
            frame = new JFrame();

            String title = "";
            if (acc != null) {
                title += acc;
            }
            if (proxy != null) {
                title += " - " + proxy;
            }
            frame.setTitle(title);
            frame.add(new BotPanel(scriptManager));
            frame.setSize(500, 500);
            frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        });

        log.info("acc:" + acc);
        log.info("proxy:" + proxy);

        NavigationButton navButton = NavigationButton.builder()
                .tooltip("bot")
                .icon(ImageUtil.loadImageResource(DevToolsPlugin.class, "devtools_icon.png"))
                .priority(-420)
                .onClick(() -> frame.setVisible(!frame.isVisible()))
                .build();

        Injector injector = RuneLite.getInjector();

        injector.getInstance(ClientToolbar.class).addNavigation(navButton);

        PluginManager pluginManager = injector.getInstance(PluginManager.class);
        for (Plugin plugin : pluginManager.getPlugins()) {
            Class<? extends Plugin> pluginClass = plugin.getClass();

            boolean enabled = enabledPlugins.contains(pluginClass);

            if (enabled != pluginManager.isPluginEnabled(plugin)) {
                togglePlugin(pluginManager, plugin, enabled);
            }
        }

        ConfigManager configManager = injector.getInstance(ConfigManager.class);
        for (ManagedConfig<?> managedConfig : managedConfigs) {
            Object currConfig = configManager.getConfiguration(managedConfig.getGroupName(), managedConfig.getKey(), managedConfig.getType());
            if (!managedConfig.config.equals(currConfig)) {
                log.info("changing config {}:{} from {} to {}", managedConfig.getGroupName(), managedConfig.getKey(), currConfig, managedConfig.getConfig());
                configManager.setConfiguration(managedConfig.getGroupName(), managedConfig.getKey(), managedConfig.getConfig());
            }
        }

        Method instantiateM = PluginManager.class.getDeclaredMethod("instantiate", List.class, Class.class);
        boolean access = instantiateM.isAccessible();
        if (!access) {
            instantiateM.setAccessible(true);
        }
        DevToolsPlugin devTools;
        try {
            //noinspection RedundantCast
            devTools = (DevToolsPlugin) instantiateM.invoke(pluginManager, (List<Plugin>) pluginManager.getPlugins(), DevToolsPlugin.class);
        } finally {
            if (!access) {
                instantiateM.setAccessible(false);
            }
        }

        pluginManager.getPlugins().add(devTools);
        togglePlugin(pluginManager, devTools, true);

//        DevToolsPlugin devToolsPlugin = DevToolsPlugin.class.getDeclaredConstructor().newInstance();
//        Module devToolsModule = binder -> {
//            binder.bind(DevToolsPlugin.class).toInstance(devToolsPlugin);
//            binder.install(devToolsPlugin);
//        };
//        Injector devToolsInjector = injector.createChildInjector(devToolsModule);
//        Field injectorField = Plugin.class.getDeclaredField("injector");
//        boolean access = injectorField.isAccessible();
//        if (!access) {
//            injectorField.setAccessible(true);
//        }
//        injectorField.set(devToolsPlugin, devToolsInjector);
//        if (!access) {
//            injectorField.setAccessible(false);
//        }
//        pluginManager.add(devToolsPlugin);
//        togglePlugin(pluginManager, devToolsPlugin, true);
//        SwingUtilities.invokeAndWait(() -> {
//            try {
//                pluginManager.startPlugin(devToolsPlugin);
//            } catch (PluginInstantiationException e) {
//                log.error("error starting devtools plugin", e);
//            }
//        });

        RuneliteContext.setInstance(injector.getInstance(RuneliteContext.class));

        String quickstartScript = System.getProperty("bot.script");
        if (quickstartScript != null) {
            scriptManager.startScript(quickstartScript);
        }
    }

    //from PluginListPanel#startPlugin/stopPlugin
    private static void togglePlugin(PluginManager pluginManager, Plugin plugin, boolean enabled) throws InterruptedException, InvocationTargetException {
        //runelite asserts on swing event dispatch thread
        SwingUtilities.invokeAndWait(() -> {
            pluginManager.setPluginEnabled(plugin, enabled);

            try {
                if (enabled) {
                    pluginManager.startPlugin(plugin);
                } else {
                    pluginManager.stopPlugin(plugin);
                }
            } catch (PluginInstantiationException ex) {
                log.warn("Error when {} plugin {}", enabled ? "enabling" : "disabling", plugin.getClass().getSimpleName(), ex);
            }
        });
    }
}
