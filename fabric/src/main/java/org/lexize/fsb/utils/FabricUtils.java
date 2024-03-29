package org.lexize.fsb.utils;

import net.minecraft.resources.ResourceLocation;

public class FabricUtils {
    public static ResourceLocation fromId(Identifier id) {
        return new ResourceLocation(id.namespace(), id.path());
    }
}
