package github.dqw4w9wgxcq.botapi.loader;

import javax.swing.*;

public class TrimmingJTextArea extends JTextArea {
    @Override
    public void append(String str) {
        String text = getText();
        if (text.length() > 50 * 1000) {
            setText(text.substring(text.length() - 5000));
        }
        super.append(str);
        scrollToBottom();
    }

    public void scrollToBottom() {
        setCaretPosition(getText().length());
    }
}
