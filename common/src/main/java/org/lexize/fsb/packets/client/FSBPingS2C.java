package org.lexize.fsb.packets.client;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.util.UUID;

public class FSBPingS2C implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "ping");

    private UUID sender;
    private int id;
    private byte[] data;

    public FSBPingS2C(IFriendlyByteBuf buf) {
        sender = buf.readUUID();
        id = buf.readInt();
        data = buf.readByteArray();
    }

    public FSBPingS2C(UUID sender, int id, byte[] data) {
        this.sender = sender;
        this.id = id;
        this.data = data;
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeUUID(sender);
        buf.writeInt(id);
        buf.writeByteArray(data);
    }

    public UUID getSender() {
        return sender;
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }
}
