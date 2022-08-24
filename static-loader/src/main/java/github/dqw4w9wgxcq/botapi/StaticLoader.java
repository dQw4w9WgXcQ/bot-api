package github.dqw4w9wgxcq.botapi;

import github.dqw4w9wgxcq.botapi.loader.*;
import net.runelite.api.Client;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.ui.ClientToolbar;

import javax.inject.Inject;
import javax.swing.*;

public class StaticLoader {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private EventBus eventBus;

    @Inject
    private ClientToolbar clientToolbar;

    static {
        System.out.println("static loader");
        StaticLoader o = new StaticLoader();
        RuneLite.getInjector().injectMembers(o);

        BotApiContext.initialize(o.client, o.clientThread, o.eventBus);
        ScriptManager scriptManager = new ScriptManager(ClassLoader.getSystemClassLoader());

        String scriptName = System.getProperty("bot.script");
        if (scriptName != null) {
            startScript(scriptManager, scriptName);
        }

        JFrame frame = new JFrame();
        frame.add(new BotPanel(scriptManager));
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private static void startScript(ScriptManager scriptManager, String scriptName) {
        for (Class<? extends IBotScript> scriptClass : scriptManager.loadScripts()) {
            if (scriptClass.getAnnotation(ScriptMeta.class).value().trim().equalsIgnoreCase(scriptName.trim())) {
                scriptManager.startScript(scriptClass);
                return;
            }
        }

        throw new IllegalArgumentException("script not found: " + scriptName);
    }
}