package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

public class FSBPingC2S implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "ping");
    private int id;
    private boolean sync;
    private byte[] data;

    public FSBPingC2S(IFriendlyByteBuf buf) {
        id = buf.readInt();
        sync = buf.readByte() == 1;
        data = buf.readByteArray();
    }

    public FSBPingC2S(int id, boolean sync, byte[] data) {
        this.id = id;
        this.sync = sync;
        this.data = data;
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeByte(sync ? 1 : 0);
        buf.writeByteArray(data);
    }

    public int getId() {
        return id;
    }

    public boolean shouldSync() {
        return sync;
    }

    public byte[] getData() {
        return data;
    }
}
