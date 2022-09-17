package github.dqw4w9wgxcq.botapi.loader;

import net.runelite.api.Client;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.ClientUI;

import javax.inject.Inject;
import java.io.File;

public class RuneliteContext {
    public static final File dir = new File(RealUserHome.getUserHome(), "runelite-bot");

    private static RuneliteContext instance = null;

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private EventBus eventBus;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private ConfigManager configManager;

    static void init() {
        RuneliteContext ctx = new RuneliteContext();
        RuneLite.getInjector().injectMembers(ctx);
        if (ctx.client == null) {
            throw new RuntimeException("outdated (client is null)");
        }
        instance = ctx;
    }

    public static RuneliteContext get() {
        if (instance == null) {
            throw new IllegalStateException("context not set");
        }

        return instance;
    }

    public static Client getClient() {
        return get().client;
    }

    public static ClientThread getClientThread() {
        return get().clientThread;
    }

    public static EventBus getEventBus() {
        return get().eventBus;
    }

    public static PluginManager getPluginManager() {
        return get().pluginManager;
    }

    public static ConfigManager getConfigManager() {
        return get().configManager;
    }
}