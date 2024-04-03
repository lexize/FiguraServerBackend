package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FSBAvatarPartC2S implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "avatar_part");
    private byte[] data;
    private String id;
    private String ehash;

    public FSBAvatarPartC2S(byte[] data, String id, String ehash) {
        this.ehash = ehash;
        this.id = id;
        this.data = data;
    }

    public FSBAvatarPartC2S(IFriendlyByteBuf buf) {
        if (buf.readByte() == 1) {
            this.ehash = new String(buf.readByteArray(), StandardCharsets.UTF_8);
        }
        this.id = new String(buf.readByteArray(), StandardCharsets.UTF_8);
        this.data = buf.readByteArray();
    }

    @Override
    public Identifier getIdentifier() {
        return null;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeByte(ehash != null ? 1 : 0);
        if (ehash != null) {
            buf.writeByteArray(ehash.getBytes(StandardCharsets.UTF_8));
        }
        buf.writeByteArray(id.getBytes(StandardCharsets.UTF_8));
        buf.writeByteArray(data);
    }

    public boolean isFinal() {
        return ehash != null;
    }

    public byte[] getData() {
        return data;
    }

    public String getId() {
        return id;
    }
}
