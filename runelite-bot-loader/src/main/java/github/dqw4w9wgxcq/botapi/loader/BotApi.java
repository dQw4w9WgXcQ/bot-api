package github.dqw4w9wgxcq.botapi.loader;

import javax.swing.*;

public class BotApi {
    private static JFrame frame = null;

    private static boolean quickstarted = false;

    public static void init(ClassLoader classLoader) {
        RuneliteContext.init();
        ScriptManager scriptManager = new ScriptManager(classLoader);

        String scriptName = System.getProperty("bot.script");
        if (!quickstarted && scriptName != null) {
            scriptManager.startScript(scriptName);
            quickstarted = true;
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
