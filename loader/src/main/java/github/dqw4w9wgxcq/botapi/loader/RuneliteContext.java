package github.dqw4w9wgxcq.botapi.loader;

import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;
import java.io.File;

public class RuneliteContext {
    public static final File dir = new File(RealUserHome.getUserHome(), "runelite-bot");

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private EventBus eventBus;

    private static RuneliteContext instance = null;

    static void setInstance(RuneliteContext ctx) {
        if (ctx.client == null) {
            throw new IllegalStateException("client null");
        }

        if (ctx.clientThread == null) {
            throw new IllegalStateException("clientThread null");
        }

        if (ctx.eventBus == null) {
            throw new IllegalStateException("eventBus null");
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
}