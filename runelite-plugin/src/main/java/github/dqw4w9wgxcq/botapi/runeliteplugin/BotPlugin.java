package github.dqw4w9wgxcq.botapi.runeliteplugin;

import github.dqw4w9wgxcq.botapi.loader.IBotScript;
import github.dqw4w9wgxcq.botapi.loader.BotApiContext;
import github.dqw4w9wgxcq.botapi.loader.ScriptManager;
import github.dqw4w9wgxcq.botapi.loader.ScriptMeta;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.util.Optional;

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

    @Override
    protected void startUp() {
        BotApiContext.initialize(client, clientThread, eventBus);
        ScriptManager scriptManager = new ScriptManager(getClass().getClassLoader());

        String scriptName = System.getProperty("bot.script");
        if (scriptName != null) {
            startScript(scriptManager, scriptName);
        }

        clientToolbar.addNavigation(NavigationButton
                .builder().tooltip("Bot")
                .icon(ImageUtil.loadImageResource(ConfigPlugin.class, "config_icon.png"))
                .priority(-420)
                .panel(new BotPanel(scriptManager))
                .build());
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
        throw new IllegalStateException("bot plugin should never be shut down");
    }
}
