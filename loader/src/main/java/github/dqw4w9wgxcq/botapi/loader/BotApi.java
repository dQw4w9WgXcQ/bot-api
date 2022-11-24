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
import net.runelite.client.plugins.entityhider.EntityHiderConfig;
import net.runelite.client.plugins.fps.FpsPlugin;
import net.runelite.client.plugins.gpu.GpuPluginConfig;
import net.runelite.client.plugins.info.InfoPlugin;
import net.runelite.client.plugins.loginscreen.LoginScreenPlugin;
import net.runelite.client.plugins.lowmemory.LowMemoryPlugin;
import net.runelite.client.plugins.menuentryswapper.MenuEntrySwapperPlugin;
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
            MenuEntrySwapperPlugin.class,
            AntiDragPlugin.class,
            //HiscorePlugin.class,
            InfoPlugin.class,
            //XpTrackerPlugin.class,
            LowMemoryPlugin.class,
            FpsPlugin.class,
            //EntityHiderPlugin.class
            //GpuPlugin.class
            LoginScreenPlugin.class
    ));

    private static final List<ManagedConfig<?>> managedConfigs = Arrays.asList(
            new ManagedConfig<>(RuneLiteConfig.GROUP_NAME, "rememberScreenBounds", Boolean.class, false),
            new ManagedConfig<>(RuneLiteConfig.GROUP_NAME, "uiEnableCustomChrome", Boolean.class, false),//needs restart
            new ManagedConfig<>(RuneLiteConfig.GROUP_NAME, "warningOnExit", WarningOnExit.class, WarningOnExit.NEVER),
            new ManagedConfig<>(RuneLiteConfig.GROUP_NAME, "trayIcon", Boolean.class, false),//needs restart
            new ManagedConfig<>(RuneLiteConfig.GROUP_NAME, "notificationTray", Boolean.class, false),

            new ManagedConfig<>("fpscontrol", "limitFps", Boolean.class, true),
            new ManagedConfig<>("fpscontrol", "maxFps", Integer.class, 30),

            new ManagedConfig<>("antiDrag", "onShiftOnly", Boolean.class, false),

            new ManagedConfig<>(GpuPluginConfig.GROUP, "drawDistance", Integer.class, 1),
            new ManagedConfig<>(GpuPluginConfig.GROUP, "useComputeShaders", Boolean.class, false),
            new ManagedConfig<>(GpuPluginConfig.GROUP, "vsyncMode", GpuPluginConfig.SyncMode.class, GpuPluginConfig.SyncMode.OFF),
            new ManagedConfig<>(GpuPluginConfig.GROUP, "unlockFps", Boolean.class, false),
            new ManagedConfig<>(GpuPluginConfig.GROUP, "fpsTarget", Integer.class, 30),

            new ManagedConfig<>(EntityHiderConfig.GROUP, "hideNPCs", Boolean.class, true),
            new ManagedConfig<>(EntityHiderConfig.GROUP, "hidePets", Boolean.class, true),
            new ManagedConfig<>(EntityHiderConfig.GROUP, "hidePlayers", Boolean.class, true),
            new ManagedConfig<>(EntityHiderConfig.GROUP, "hideProjectiles", Boolean.class, true),
            new ManagedConfig<>(EntityHiderConfig.GROUP, "hideLocalPlayer", Boolean.class, true),
            new ManagedConfig<>(EntityHiderConfig.GROUP, "hideNPCs", Boolean.class, true),

            new ManagedConfig<>("loginscreen", "showLoginFire", Boolean.class, false)
    );

    @SneakyThrows
    public static void init(ClassLoader rlLoader) {
        String acc = System.getProperty("bot.acc");

        scriptManager = new ScriptManager(RuneLite.class.getClassLoader());

        SwingUtilities.invokeAndWait(() -> {
            frame = new JFrame();

            String title = "";
            if (acc != null) {
                title += acc;
            }

            frame.setTitle(title);
            frame.add(new BotPanel(scriptManager));
            frame.setSize(500, 500);
            frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        });

        log.info("acc:" + acc);

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

        Method instantiate = PluginManager.class.getDeclaredMethod("instantiate", List.class, Class.class);
        @SuppressWarnings("deprecation")
        boolean access = instantiate.isAccessible();
        if (!access) {
            instantiate.setAccessible(true);
        }
        DevToolsPlugin devTools;
        try {
            //noinspection RedundantCast
            devTools = (DevToolsPlugin) instantiate.invoke(pluginManager, (List<Plugin>) pluginManager.getPlugins(), DevToolsPlugin.class);
        } finally {
            if (!access) {
                instantiate.setAccessible(false);
            }
        }

        pluginManager.getPlugins().add(devTools);
        togglePlugin(pluginManager, devTools, true);

        RuneliteContext.setInstance(injector.getInstance(RuneliteContext.class));

        String quickstartScript = System.getProperty("bot.script");
        if (quickstartScript != null) {
            scriptManager.startScript(quickstartScript);
        }
    }

    public static void toggleFrame(boolean open) {
        SwingUtilities.invokeLater(() -> frame.setVisible(open));
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
