package github.dqw4w9wgxcq.botapi.injector;

import github.dqw4w9wgxcq.botapi.injector.injectors.SocketInjector;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Inject {
    private static List<Injector> injectors = null;

    //gets called by injected guava com.google.common.io.ByteStreams
    public static byte[] inject(byte[] bytes) {
        if (!Thread.currentThread().getName().equals("Preloader")) {
            return bytes;
        }

        if (injectors == null) {
            initInjectors();
        }

        String name = getClassInternalName(bytes);

        for (Injector injector : injectors) {
            bytes = injector.inject(name, bytes);
        }

        return bytes;
    }

    private static void initInjectors() {
        injectors = Arrays.asList(
                new SocketInjector("fp")
//                new PlatformInfoInjector()
        );
    }

    //internal means / instead of .
    private static String getClassInternalName(byte[] bytes) {
        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.SKIP_FRAMES | ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG);
        return classNode.name;
    }
}
