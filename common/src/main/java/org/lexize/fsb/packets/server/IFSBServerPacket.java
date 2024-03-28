package org.lexize.fsb.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.lexize.fsb.FSBServer;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.util.UUID;

public interface IFSBServerPacket {
    Identifier getIdentifier();
    void write(IFriendlyByteBuf buf);
    void handle(FSBServer server, UUID sender);
}
