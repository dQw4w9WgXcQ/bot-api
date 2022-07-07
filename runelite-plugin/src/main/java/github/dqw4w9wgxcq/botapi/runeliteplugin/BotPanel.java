package github.dqw4w9wgxcq.botapi.runeliteplugin;

import github.dqw4w9wgxcq.botapi.loader.IBotScript;
import github.dqw4w9wgxcq.botapi.loader.ScriptManager;
import github.dqw4w9wgxcq.botapi.loader.ScriptMeta;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class BotPanel extends PluginPanel {
    private final JList<ScriptListEntry> scriptList = new JList<>();
    private final JFrame logFrame;
    private final ScriptManager scriptManager;

    public BotPanel(ScriptManager scriptManager) {
        super(false);
        this.scriptManager = scriptManager;

        setLayout(new BorderLayout());

        refreshScriptList();

        //north
        Panel northPanel = new Panel(new GridLayout(1, 0));

        //startButton
        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            ScriptListEntry entry = scriptList.getSelectedValue();
            if (entry != null) {
                scriptManager.startScript(entry.getScriptClass());
            }
        });
        northPanel.add(startButton);

        //stopButton
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> {
            scriptManager.stopScript();
            refreshScriptList();
        });
        northPanel.add(stopButton);

        //logButton
        JButton logButton = new JButton("Logger");
        logButton.addActionListener(e -> openLogger());
        northPanel.add(logButton);

        //paint
//		JButton drawMouseButton = new JButton("Paint");
//		drawMouseButton.addActionListener(e -> BotOverlay.togglePaint());
//		northPanel.add(drawMouseButton);

        add(northPanel, BorderLayout.NORTH);

        //center
        Panel centerPanel = new Panel(new GridLayout(0, 1));

        //scriptScrollPane
        JScrollPane scriptScrollPane = new JScrollPane(scriptList);
        scriptScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        centerPanel.add(scriptScrollPane);

        add(centerPanel, BorderLayout.CENTER);

        logFrame = new JFrame();

        JTextArea textArea = new TrimmingJTextArea();
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        logFrame.getContentPane().add(scrollPane);
        logFrame.pack();
        logFrame.setSize(1000, 1000);

        System.setOut(new PrintStreamInterceptor(System.out, textArea));
        System.setErr(new PrintStreamInterceptor(System.err, textArea));
    }

    private void openLogger() {
        logFrame.setVisible(!logFrame.isVisible());
    }

    private void refreshScriptList() {
        Vector<ScriptListEntry> scriptListEntries = new Vector<>();
        for (Class<? extends IBotScript> scriptClass : scriptManager.loadScripts()) {
            scriptListEntries.add(new ScriptListEntry(scriptClass));
        }

        scriptList.setListData(scriptListEntries);
    }

    private static final class ScriptListEntry {
        private final Class<? extends IBotScript> scriptClass;
        private final ScriptMeta meta;

        private ScriptListEntry(Class<? extends IBotScript> scriptClass, ScriptMeta meta) {
            this.scriptClass = scriptClass;
            this.meta = meta;
        }

        private ScriptListEntry(Class<? extends IBotScript> scriptClass) {
            this(scriptClass, scriptClass.getAnnotationsByType(ScriptMeta.class)[0]);
        }

        @Override
        public String toString() {
            return meta.value();
        }

        public Class<? extends IBotScript> getScriptClass() {
            return scriptClass;
        }
    }
}
