package github.dqw4w9wgxcq.botapi.runeliteplugin;

import github.dqw4w9wgxcq.botapi.loader.BotApi;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(name = "bot")
@Slf4j
public class BotPlugin extends Plugin {
    @Override
    protected void startUp() {
        BotApi.init(getClass().getClassLoader());
    }

    @Override
    protected void shutDown() {
        BotApi.shutDown();
    }
}
