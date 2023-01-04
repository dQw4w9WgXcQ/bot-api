package github.dqw4w9wgxcq.botapi.injector.injectors;

import github.dqw4w9wgxcq.botapi.injector.Injector;
import github.dqw4w9wgxcq.botapi.injector.NonloadingClassWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class SocketInjector implements Injector {
    private final String taskHandlerClassName;

    public SocketInjector(String taskHandlerClassName) {
        this.taskHandlerClassName = taskHandlerClassName;
    }

    @Override
    public byte[] inject(String className, byte[] bytes) {
        if (!className.equals(taskHandlerClassName)) return bytes;

        ClassNode cn = new ClassNode();
        ClassReader cr = new ClassReader(bytes);
        cr.accept(cn, 0);

        MethodNode mn = cn.methods.stream()
                .filter(m -> m.name.equals("run"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find run method in " + className));

        MethodInsnNode initSocketInsn = null;
        for (AbstractInsnNode insn : mn.instructions) {
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;
                if (methodInsn.owner.equals("java/net/Socket") && methodInsn.name.equals("<init>")) {
                    initSocketInsn = methodInsn;
                    break;
                }
            }
        }
        if (initSocketInsn == null) throw new RuntimeException("Could not find Socket inits");

        mn.instructions.insertBefore(
                initSocketInsn,
                new MethodInsnNode(Opcodes.INVOKESTATIC, "rl10", "socket", "(Ljava/net/InetAddress;I)Ljava/net/Socket;")
        );

        mn.instructions.remove(initSocketInsn);

        AbstractInsnNode newSocketInsn = null;
        for (AbstractInsnNode insn : mn.instructions) {
            if (insn.getOpcode() == Opcodes.NEW && ((TypeInsnNode) insn).desc.equals("java/net/Socket")) {
                newSocketInsn = insn;
                break;
            }
        }
        if (newSocketInsn == null) throw new RuntimeException("Could not find newSocket instruction");
        mn.instructions.remove(newSocketInsn.getNext());//DUP
        mn.instructions.remove(newSocketInsn);

        ClassWriter cw = new NonloadingClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);

        return cw.toByteArray();
    }
}
