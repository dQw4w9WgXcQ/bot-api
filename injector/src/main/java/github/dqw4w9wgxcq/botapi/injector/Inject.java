package github.dqw4w9wgxcq.botapi.injector;

import github.dqw4w9wgxcq.botapi.injector.injectors.ProxySocket;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

@Slf4j
public class Inject {
    public static List<Injector> injectors = List.of(
            new ProxySocket("fp")
    );

    //gets called by injected guava com.google.common.io.ByteStreams
    public static byte[] inject(byte[] bytes) {
        if (!Thread.currentThread().getName().equals("Preloader")) {
            return bytes;
        }

        ClassReader cr = new ClassReader(bytes);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        String name = cn.name.replace("/", ".");

        for (Injector injector : injectors) {
            injector.inject(name, cn);
        }

        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        return cw.toByteArray();
    }
}
