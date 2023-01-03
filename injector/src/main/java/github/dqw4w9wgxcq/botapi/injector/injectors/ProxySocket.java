package github.dqw4w9wgxcq.botapi.injector.injectors;

import github.dqw4w9wgxcq.botapi.injector.Injector;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ProxySocket implements Injector {
    private final String taskHandlerClassName;

    public ProxySocket(String taskHandlerClassName) {
        this.taskHandlerClassName = taskHandlerClassName;
    }

    @Override
    public void inject(String className, ClassNode cn) {
        if (className.equals(taskHandlerClassName)) return;

        InsnList instructions = cn.methods.stream()
                .filter(m -> m.name.equals("run"))
                .map(m -> m.instructions)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find run method in " + className));

        MethodInsnNode initSocketInsn = null;
        for (AbstractInsnNode insn : instructions) {
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;
                if (methodInsn.owner.equals("java/net/Socket") && methodInsn.name.equals("<init>")) {
                    initSocketInsn = methodInsn;
                    break;
                }
            }
        }
        if (initSocketInsn == null) throw new RuntimeException("Could not find Socket constructor");

        instructions.insertBefore(
                initSocketInsn,
                new MethodInsnNode(Opcodes.INVOKESTATIC, "github/dqw4w9wgxcq/botapi/mixins/Proxy", "initSocket", "(Ljava/net/InetAddress;I)V", false)
        );

        instructions.remove(initSocketInsn);
    }
}
