package github.dqw4w9wgxcq.botapi.injector.injectors;

import github.dqw4w9wgxcq.botapi.injector.FixedClassWriter;
import github.dqw4w9wgxcq.botapi.injector.Injector;
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

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        MethodNode runMethod = classNode.methods.stream()
                .filter(m -> m.name.equals("run"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find run method in " + className));

        MethodInsnNode initSocketInsn = null;
        for (AbstractInsnNode insn : runMethod.instructions) {
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;
                if (methodInsn.owner.equals("java/net/Socket") && methodInsn.name.equals("<init>")) {
                    initSocketInsn = methodInsn;
                    break;
                }
            }
        }
        if (initSocketInsn == null) throw new RuntimeException("Could not find Socket init insn");

        runMethod.instructions.insertBefore(
                initSocketInsn,
                new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "github/dqw4w9wgxcq/botapi/injector/Mixins",
                        "createSocket",
                        "(Ljava/net/InetAddress;I)Ljava/net/Socket;"
                )
        );

        runMethod.instructions.remove(initSocketInsn);

        TypeInsnNode newSocketInsn = null;
        for (AbstractInsnNode insn : runMethod.instructions) {
            if (insn.getOpcode() == Opcodes.NEW && ((TypeInsnNode) insn).desc.equals("java/net/Socket")) {
                newSocketInsn = (TypeInsnNode) insn;
                break;
            }
        }
        if (newSocketInsn == null) throw new RuntimeException("Could not find newSocket instruction");

        runMethod.instructions.remove(newSocketInsn.getNext());//DUP
        runMethod.instructions.remove(newSocketInsn);

        ClassWriter classWriter = new FixedClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);

        return classWriter.toByteArray();
    }
}
