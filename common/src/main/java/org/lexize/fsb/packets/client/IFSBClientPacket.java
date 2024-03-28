package org.lexize.fsb.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.lexize.fsb.FSBClient;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

public interface IFSBClientPacket {
    Identifier getIdentifier();
    void write(IFriendlyByteBuf buf);
    void handle(FSBClient client);
}
