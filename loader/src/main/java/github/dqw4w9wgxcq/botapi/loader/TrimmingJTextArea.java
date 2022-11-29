package github.dqw4w9wgxcq.botapi.loader;

import javax.swing.*;

public class TrimmingJTextArea extends JTextArea {
    @Override
    public void append(String str) {
        String text = getText();
        if (text.length() > 20000) {
            setText(text.substring(text.length() - 10000));
        }
        super.append(str);
        scrollToBottom();
    }

    public void scrollToBottom() {
        getCaret().setDot(Integer.MAX_VALUE);
    }
}
