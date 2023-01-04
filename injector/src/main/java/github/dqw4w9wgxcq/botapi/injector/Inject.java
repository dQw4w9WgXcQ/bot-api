package github.dqw4w9wgxcq.botapi.injector;

import github.dqw4w9wgxcq.botapi.injector.injectors.SocketInjector;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Inject {
    public static List<Injector> injectors = Arrays.asList(
            new SocketInjector("fp")
    );

    //gets called by injected guava com.google.common.io.ByteStreams
    public static byte[] inject(byte[] bytes) {
        if (!Thread.currentThread().getName().equals("Preloader")) {
            return bytes;
        }

        ClassReader cr = new ClassReader(bytes);
        ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.SKIP_FRAMES);
        String name = cn.name;
        if (!name.equals("fp")) {
            return bytes;
        }

        for (Injector injector : injectors) {
            bytes = injector.inject(name, bytes);
        }

        return bytes;
    }
}
