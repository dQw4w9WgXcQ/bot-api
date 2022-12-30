package github.dqw4w9wgxcq.bot.injector;

import github.dqw4w9wgxcq.bot.injector.disablerendering.DisableRendering;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

@Slf4j
public class Inject {
	public static List<Injector> injectors = List.of(
			new DisableRendering()
	);

	//gets called by injected guava com.google.common.io.ByteStreams
	public static byte[] inject(byte[] bytes) {
		if (!Thread.currentThread().getName().equals("Preloader")) {
			return bytes;
		}

		var cr = new ClassReader(bytes);
		var cn = new ClassNode();
		cr.accept(cn, 0);
		var name = cn.name.replace("/", ".");

		for (var injector : injectors) {
			bytes = injector.inject(name, bytes);
		}

		return bytes;
	}
}
