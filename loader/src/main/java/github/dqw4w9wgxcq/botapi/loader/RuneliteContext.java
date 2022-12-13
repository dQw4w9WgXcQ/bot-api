package github.dqw4w9wgxcq.botapi.loader;

import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

public class RuneliteContext {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private EventBus eventBus;
    @Inject
    private OverlayManager overlayManager;

    private static RuneliteContext instance = null;

    static void setInstance(RuneliteContext ctx) {
        if (ctx.client == null) {
            throw new IllegalStateException("client null, prob update");
        }

        instance = ctx;
    }

    static RuneliteContext getInstance() {
        if (instance == null) {
            throw new IllegalStateException("context not set");
        }

        return instance;
    }

    public static Client getClient() {
        return getInstance().client;
    }

    public static ClientThread getClientThread() {
        return getInstance().clientThread;
    }

    public static EventBus getEventBus() {
        return getInstance().eventBus;
    }

    public static OverlayManager getOverlayManager() {
        return getInstance().overlayManager;
    }
}