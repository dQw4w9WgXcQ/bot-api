package github.dqw4w9wgxcq.bot.injector;

public interface Injector {
    byte[] inject(String className, byte[] bytes);
}
