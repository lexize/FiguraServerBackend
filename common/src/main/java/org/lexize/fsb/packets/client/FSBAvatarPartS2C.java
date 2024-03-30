package org.lexize.fsb.packets.client;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FSBAvatarPartS2C implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "avatar_part");
    private UUID owner;
    private boolean isFinal;
    private byte[] data;
    private String hash;

    public FSBAvatarPartS2C(UUID owner, boolean isFinal, byte[] data, String hash) {
        this.owner = owner;
        this.isFinal = isFinal;
        this.data = data;
    }

    public FSBAvatarPartS2C(IFriendlyByteBuf buf) {
        this.owner = buf.readUUID();
        this.isFinal = buf.readByte() == 1;
        if (buf.readByte() == 1) {
            this.hash = new String(buf.readByteArray(), StandardCharsets.UTF_8);
        }
        this.data = buf.readByteArray();
    }

    @Override
    public Identifier getIdentifier() {
        return null;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeUUID(owner);
        buf.writeByte(isFinal ? 1 : 0);
        if (hash != null) {
            buf.writeByte(1);
            buf.writeByteArray(hash.getBytes(StandardCharsets.UTF_8));
        }
        else {
            buf.writeByte(0);
        }
        buf.writeByteArray(data);
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public byte[] getData() {
        return data;
    }

    public String getHash() {
        return hash;
    }
}
