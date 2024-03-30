package org.lexize.fsb;

import org.lexize.fsb.packets.IFSBPacket;

import java.util.UUID;

public abstract class FSBServer {
    protected abstract void initializeServerPackets();
    public abstract void sendS2CPacket(UUID receiver, IFSBPacket packet);
}
