package org.lexize.fsb.utils;

import net.minecraft.resources.ResourceLocation;

public interface IIdentifier {
    default ResourceLocation toResourceLocation() {
        if (this instanceof Identifier id) {
            return new ResourceLocation(id.namespace(), id.path());
        }
        return null;
    }
}
