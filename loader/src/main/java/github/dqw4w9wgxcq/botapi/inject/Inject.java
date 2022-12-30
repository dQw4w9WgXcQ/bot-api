package github.dqw4w9wgxcq.botapi.inject;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

//gets called by injected guava com.google.common.io.ByteStreams
@Slf4j
public class Inject {
	public static byte[] inject(byte[] bytes) {
		if (!Thread.currentThread().getName().equals("Preloader")) {
			log.info("InjectRs: " + Thread.currentThread().getName());
			return bytes;
		}

		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		log.info("cn.name: {}", cn.name);
		return bytes;
	}
}
