package github.dqw4w9wgxcq.botapi.injector;

import org.objectweb.asm.tree.ClassNode;

public interface Injector {
    void inject(String className, ClassNode cn);
}
