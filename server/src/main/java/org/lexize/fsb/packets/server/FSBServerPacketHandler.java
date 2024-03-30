package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSBServer;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;

import java.util.UUID;

public abstract class FSBServerPacketHandler<T extends IFSBPacket> {
    protected final FSBServer parent;

    protected FSBServerPacketHandler(FSBServer parent) {
        this.parent = parent;
    }

    public abstract void handle(UUID player, T packet);
    public abstract T serialize(IFriendlyByteBuf buf);
}
