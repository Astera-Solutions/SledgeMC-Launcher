/**
 * @author Tinkoprof
 * @summary Interface defining the contract for ASM-based class bytecode transformers.
 */
package sledgemc.dev.transform;

public interface ClassTransformer {

    boolean shouldTransform(String className);

    byte[] transform(String className, byte[] classBytes);

    default String getName() {
        return getClass().getSimpleName();
    }

    default int getPriority() {
        return 1000;
    }
}
