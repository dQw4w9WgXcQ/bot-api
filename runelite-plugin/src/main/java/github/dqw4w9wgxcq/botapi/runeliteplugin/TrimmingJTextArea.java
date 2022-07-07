package github.dqw4w9wgxcq.botapi.runeliteplugin;

import javax.swing.JTextArea;

public class TrimmingJTextArea extends JTextArea
{
	@Override
	public void append(String str)
	{
		String text = getText();
		if (text.length() > 50000)
		{
			setText(text.substring(text.length() - 5000));
		}
		super.append(str);
		setCaretPosition(getText().length());
	}
}
