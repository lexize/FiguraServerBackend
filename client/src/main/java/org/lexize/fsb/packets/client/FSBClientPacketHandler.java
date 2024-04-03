package org.lexize.fsb.packets.client;

import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;

public abstract class FSBClientPacketHandler<T extends IFSBPacket> {
    protected final FSBClient parent;

    public FSBClientPacketHandler(FSBClient parent) {
        this.parent = parent;
    }

    public abstract void handle(T packet);
    public abstract T serialize(IFriendlyByteBuf buf);
}
