package github.dqw4w9wgxcq.botapi.injector;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Inject {
    public static List<Injector> injectors = Arrays.asList(
//            new SocketInjector("fp")
    );

    //gets called by injected guava com.google.common.io.ByteStreams
    public static byte[] inject(byte[] bytes) {
        if (!Thread.currentThread().getName().equals("Preloader")) {
            return bytes;
        }

        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
        String name = classNode.name;

        for (Injector injector : injectors) {
            bytes = injector.inject(name, bytes);
        }

        return bytes;
    }
}
