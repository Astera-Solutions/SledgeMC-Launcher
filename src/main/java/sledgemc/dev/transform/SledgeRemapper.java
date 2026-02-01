/**
 * @author Tinkoprof
 * @summary An adapter class that integrates the MappingService with the Mixin library's remapping system.
 */
package sledgemc.dev.transform;

import org.spongepowered.asm.mixin.extensibility.IRemapper;

public class SledgeRemapper implements IRemapper {

    private final MappingService mappingService;

    public SledgeRemapper(MappingService mappingService) {
        this.mappingService = mappingService;
    }

    @Override
    public String mapMethodName(String owner, String name, String desc) {
        return mappingService.mapMethodName(owner, name, desc);
    }

    @Override
    public String mapFieldName(String owner, String name, String desc) {
        return mappingService.mapFieldName(owner, name);
    }

    @Override
    public String map(String typeName) {
        return mappingService.mapClassName(typeName);
    }

    @Override
    public String unmap(String typeName) {
        return typeName;
    }

    @Override
    public String mapDesc(String desc) {
        return desc;
    }

    @Override
    public String unmapDesc(String desc) {
        return desc;
    }
}
