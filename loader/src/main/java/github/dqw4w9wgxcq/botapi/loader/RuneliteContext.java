package github.dqw4w9wgxcq.botapi.loader;

import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;

import java.io.File;

public class RuneliteContext {
    public static final File dir = new File(RealUserHome.getUserHome(), "runelite-bot");

    @Setter
    private static RuneliteContext instance = null;

    private Client client;
    private ClientThread clientThread;
    private EventBus eventBus;

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