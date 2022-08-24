package github.dqw4w9wgxcq.botapi.runeliteplugin;

import github.dqw4w9wgxcq.botapi.loader.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;

import javax.inject.Inject;
import javax.swing.*;

@PluginDescriptor(name = "bot")
@Slf4j
public class BotPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private EventBus eventBus;

    @Inject
    private ClientToolbar clientToolbar;

    private JFrame frame;

    @Override
    protected void startUp() {
        BotApiContext.initialize(client, clientThread, eventBus);
        ScriptManager scriptManager = new ScriptManager(getClass().getClassLoader());

        String scriptName = System.getProperty("bot.script");
        if (scriptName != null) {
            startScript(scriptManager, scriptName);
        }

        frame = new JFrame();
        frame.add(new BotPanel(scriptManager));
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private void startScript(ScriptManager scriptManager, String scriptName) {
        for (Class<? extends IBotScript> scriptClass : scriptManager.loadScripts()) {
            if (scriptClass.getAnnotation(ScriptMeta.class).value().trim().equalsIgnoreCase(scriptName.trim())) {
                scriptManager.startScript(scriptClass);
                return;
            }
        }

        throw new IllegalArgumentException("script not found: " + scriptName);
    }

    @Override
    protected void shutDown() {
        frame.dispose();
    }
}
