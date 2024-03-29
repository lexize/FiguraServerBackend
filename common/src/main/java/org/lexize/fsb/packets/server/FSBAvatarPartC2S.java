package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.util.UUID;

public class FSBAvatarPartC2S implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "avatar_part");
    private boolean isFinal;
    private byte[] data;

    public FSBAvatarPartC2S(boolean isFinal, byte[] data) {
        this.isFinal = isFinal;
        this.data = data;
    }

    public FSBAvatarPartC2S(IFriendlyByteBuf buf) {
        this.isFinal = buf.readByte() == 1;
        this.data = buf.readByteArray();
    }

    @Override
    public Identifier getIdentifier() {
        return null;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeByte(isFinal ? 1 : 0);
        buf.writeByteArray(data);
    }

    public boolean isFinal() {
        return isFinal;
    }

    public byte[] getData() {
        return data;
    }
}
