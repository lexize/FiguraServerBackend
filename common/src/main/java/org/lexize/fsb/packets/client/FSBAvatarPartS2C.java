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
    private String id;

    public FSBAvatarPartS2C(UUID owner, boolean isFinal, byte[] data, String hash, String id) {
        this.owner = owner;
        this.isFinal = isFinal;
        this.id = id;
        this.data = data;
        this.hash = hash;
    }

    public FSBAvatarPartS2C(IFriendlyByteBuf buf) {
        this.owner = buf.readUUID();
        this.id = new String(buf.readByteArray(), StandardCharsets.UTF_8);
        this.isFinal = buf.readByte() == 1;
        if (this.isFinal) {
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
        if (isFinal) {
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

    public String getId() {
        return id;
    }
}
