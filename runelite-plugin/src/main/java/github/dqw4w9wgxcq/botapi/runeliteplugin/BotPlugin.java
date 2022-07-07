package github.dqw4w9wgxcq.botapi.runeliteplugin;

import github.dqw4w9wgxcq.botapi.loader.IBotScript;
import github.dqw4w9wgxcq.botapi.loader.BotApiContext;
import github.dqw4w9wgxcq.botapi.loader.ScriptManager;
import github.dqw4w9wgxcq.botapi.loader.ScriptMeta;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
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

@PluginDescriptor(
	name = "bot"
)
@Slf4j
/*
I HAVE NOT TESTED IF THIS WORKS OR EVEN COMPILES CORRECTLY
 */
public class BotPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private EventBus eventBus;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private OverlayManager overlayManager;

	private NavigationButton navButton;
//	private BotOverlay overlay;

	@Override
	protected void startUp() throws Exception
	{
		BotApiContext.INSTANCE.initialize(client, clientThread, eventBus);
		ScriptManager scriptManager = new ScriptManager();

		String scriptName = System.getProperty("bot.script");
		if (scriptName != null)
		{
			startScript(scriptManager, scriptName);
		}

		navButton = NavigationButton.builder()
			.tooltip("Bot")
			.icon(ImageUtil.loadImageResource(ConfigPlugin.class, "config_icon.png"))
			.priority(-420)
			.panel(new BotPanel(scriptManager))
			.build();

		clientToolbar.addNavigation(navButton);
//		overlayManager.add(overlay = new BotOverlay());
	}

	private void startScript(ScriptManager scriptManager, String scriptName)
	{
		for (Class<? extends IBotScript> scriptClass : scriptManager.loadScripts())
		{
			if (scriptClass.getAnnotation(ScriptMeta.class).value().equalsIgnoreCase(scriptName))
			{
				scriptManager.startScript(scriptClass);
				return;
			}
		}

		log.warn("cant find script: " + scriptName);
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
//		overlayManager.remove(overlay);
	}
}
