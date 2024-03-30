package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FSBAvatarPartC2S implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "avatar_part");
    private boolean isFinal;
    private byte[] data;
    private String id;

    public FSBAvatarPartC2S(boolean isFinal, byte[] data, String id) {
        this.isFinal = isFinal;
        this.id = id;
        this.data = data;
    }

    public FSBAvatarPartC2S(IFriendlyByteBuf buf) {
        this.isFinal = buf.readByte() == 1;
        this.id = new String(buf.readByteArray(), StandardCharsets.UTF_8);
        this.data = buf.readByteArray();
    }

    @Override
    public Identifier getIdentifier() {
        return null;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeByte(isFinal ? 1 : 0);
        buf.writeByteArray(id.getBytes(StandardCharsets.UTF_8));
        buf.writeByteArray(data);
    }

    public boolean isFinal() {
        return isFinal;
    }

    public byte[] getData() {
        return data;
    }

    public String getId() {
        return id;
    }
}
