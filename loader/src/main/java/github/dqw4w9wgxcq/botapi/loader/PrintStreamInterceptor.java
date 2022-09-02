package github.dqw4w9wgxcq.botapi.loader;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class PrintStreamInterceptor extends PrintStream {
    private final JTextArea logTextArea;

    public PrintStreamInterceptor(OutputStream originalOut, JTextArea logTextArea) {
        super(originalOut, true);
        this.logTextArea = logTextArea;
    }

    @Override
    public void write(byte[] b) throws IOException {
        logTextArea.append(new String(b));
        super.write(b);
    }

    @Override
    public void write(int b) {
        logTextArea.append(String.valueOf(b));
        super.write(b);
    }

    @Override
    public void println(boolean b) {
        logTextArea.append(b + "\n");
        super.println(b);
    }

    @Override
    public void println(char c) {
        logTextArea.append(c + "\n");
        super.println(c);
    }

    @Override
    public void println(int i) {
        logTextArea.append(i + "\n");
        super.println(i);
    }

    @Override
    public void println(long l) {
        logTextArea.append(l + "\n");
        super.println(l);
    }

    @Override
    public void println(float f) {
        logTextArea.append(f + "\n");
        super.println(f);
    }

    @Override
    public void println(double d) {
        logTextArea.append(d + "\n");
        super.println(d);
    }

    @Override
    public void println(String s) {
        logTextArea.append(s + "\n");
        super.println(s);
    }

    @Override
    public void println(Object obj) {
        logTextArea.append(obj + "\n");
        super.println(obj);
    }

    @Override
    public void println() {
        logTextArea.append("\n");
        super.println();
    }
}
