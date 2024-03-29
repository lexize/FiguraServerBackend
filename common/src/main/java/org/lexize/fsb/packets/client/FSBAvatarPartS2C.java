package org.lexize.fsb.packets.client;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.util.UUID;

public class FSBAvatarPartS2C implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "avatar_part");
    private UUID owner;
    private boolean isFinal;
    private byte[] data;

    public FSBAvatarPartS2C(UUID owner, boolean isFinal, byte[] data) {
        this.owner = owner;
        this.isFinal = isFinal;
        this.data = data;
    }

    public FSBAvatarPartS2C(IFriendlyByteBuf buf) {
        this.owner = buf.readUUID();
        this.isFinal = buf.readByte() == 1;
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
}
