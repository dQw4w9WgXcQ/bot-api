package github.dqw4w9wgxcq.botapi.injector;

public interface Injector {
    byte[] inject(String classInternalName, byte[] bytes);
}
