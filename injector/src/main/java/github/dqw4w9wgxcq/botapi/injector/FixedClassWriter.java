package github.dqw4w9wgxcq.botapi.injector;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

//https://github.com/projectlombok/lombok/blob/731bb185077918af8bc1e6a9e6bb538b2d3fbbd8/src/core/lombok/bytecode/FixedClassWriter.java
@Slf4j
public class FixedClassWriter extends ClassWriter {
    public FixedClassWriter(ClassReader classReader, int flags) {
        super(classReader, flags);
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        log.debug("getCommonSuperClass: {} {}", type1, type2);

        try {
            return super.getCommonSuperClass(type1, type2);
        } catch (VirtualMachineError e) {
            throw e;
        } catch (Throwable t) {
            log.debug("super.getCommonSuperClass error", t);
            return "java/lang/Object";
        }
    }
}
