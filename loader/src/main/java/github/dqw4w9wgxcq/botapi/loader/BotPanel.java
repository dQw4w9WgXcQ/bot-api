package github.dqw4w9wgxcq.botapi.loader;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

@Slf4j
public class BotPanel extends JPanel {
    private final JList<ScriptListEntry> scriptList = new JList<>();
    private final JFrame logFrame;
    private final TrimmingJTextArea logTextArea;
    private final ScriptManager scriptManager;

    public BotPanel(ScriptManager scriptManager) {
        super(false);
        logTextArea = new TrimmingJTextArea();
        logTextArea.setEditable(false);
        System.setOut(new PrintStreamInterceptor(System.out, logTextArea));
        System.setErr(new PrintStreamInterceptor(System.err, logTextArea));

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

        //south
        Panel southPanel = new Panel(new GridLayout(0, 1));
        JTextField accountTextField = new JTextField();

        String acc = System.getProperty("bot.acc");
        if (acc != null) {
            accountTextField.setText(acc);
        }

        accountTextField.addActionListener(e -> {
            String text = accountTextField.getText();
            log.info("setting acc:" + text);
            System.setProperty("bot.acc", text);
        });

        southPanel.add(accountTextField);

        add(southPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        logFrame = new JFrame();
        logFrame.getContentPane().add(scrollPane);
        logFrame.setSize(1000, 1000);
    }

    private void openLogger() {
        logFrame.setVisible(!logFrame.isVisible());
        logTextArea.scrollToBottom();//doesnt work
    }

    private void refreshScriptList() {
        Vector<ScriptListEntry> scriptListEntries = new Vector<>();
        for (Class<? extends IBotScript> scriptClass : scriptManager.loadScripts()) {
            scriptListEntries.add(new ScriptListEntry(scriptClass));
        }

        scriptList.setListData(scriptListEntries);
    }

    private static final class ScriptListEntry {
        @Getter
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
    }
}
