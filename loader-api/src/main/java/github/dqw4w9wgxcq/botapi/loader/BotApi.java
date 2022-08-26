package github.dqw4w9wgxcq.botapi.loader;

import javax.swing.*;

public class BotApi {
    private static JFrame frame = null;

    public static void init() {
        RuneliteContext.init();
        ScriptManager scriptManager = new ScriptManager(ClassLoader.getSystemClassLoader());

        String scriptName = System.getProperty("bot.script");
        if (scriptName != null) {
            scriptManager.startScript(scriptName);
        } else {
            frame = new JFrame();
            frame.add(new BotPanel(scriptManager));
            frame.setSize(500, 500);
            frame.setVisible(true);
        }
    }

    public static void shutDown() {
        if (frame != null) {
            frame.dispose();
        }
    }
}
