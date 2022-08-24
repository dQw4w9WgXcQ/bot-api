package github.dqw4w9wgxcq.botapi.loader;

import lombok.AllArgsConstructor;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;

import java.io.File;

@AllArgsConstructor
public class BotApiContext {
    public static final File dir = new File(System.getProperty("user.home"), "runelite-bot");

    private static BotApiContext context = null;

    public static BotApiContext getContext() {
        if (context == null) {
            throw new IllegalStateException("context not set");
        }

        return context;
    }

    public static Client getClient() {
        return getContext().client;
    }

    public static ClientThread getClientThread() {
        return getContext().clientThread;
    }

    public static EventBus getEventBus() {
        return getContext().eventBus;
    }

    public static void initialize(Client client, ClientThread clientThread, EventBus eventBus) {
        context = new BotApiContext(client, clientThread, eventBus);
    }

    private Client client;
    private ClientThread clientThread;
    private EventBus eventBus;
}