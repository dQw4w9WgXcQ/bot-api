package github.dqw4w9wgxcq.botapi.loader;

import javax.swing.*;

public class BotApi {
    private static JFrame frame = null;
    private static ScriptManager scriptManager;

    public static void init(ClassLoader classLoader) {
        RuneliteContext.init();

        scriptManager = new ScriptManager(classLoader);

        SwingUtilities.invokeLater(() -> {
            frame = new JFrame();

            String title = "";
            String acc = System.getProperty("bot.acc");
            if (acc != null) {
                title += acc;
            } else {
                title = "asdf";
            }
            String proxy = System.getProperty("socksProxyHost");
            if (proxy != null) {
                title += " - " + proxy;
            }
            frame.setTitle(title);
            frame.add(new BotPanel(scriptManager));
            frame.setSize(500, 500);
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.setVisible(true);
        });

        String scriptName = System.getProperty("bot.script");
        if (scriptName != null) {
            scriptManager.startScript(scriptName);
        }
    }

    public static void shutDown() {
        if (frame != null) {
            frame.dispose();
        }

        scriptManager.stopScript();
    }
}
