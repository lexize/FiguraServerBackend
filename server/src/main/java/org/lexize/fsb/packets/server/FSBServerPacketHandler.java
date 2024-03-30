package org.lexize.fsb.packets.server;

import org.lexize.fsb.packets.IFSBPacket;

public abstract class FSBServerPacketHandler<T extends IFSBPacket> {
    public abstract void handle(T packet);
    public abstract T serialize();
}
