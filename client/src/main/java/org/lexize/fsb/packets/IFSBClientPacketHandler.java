package org.lexize.fsb.packets;

import org.lexize.fsb.utils.IFriendlyByteBuf;

public interface IFSBClientPacketHandler<T extends IFSBPacket> {
    void handle(T packet);
    T serialize(IFriendlyByteBuf buf);
}
