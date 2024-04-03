package org.lexize.fsb.packets.client;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FSBAvatarPartS2C implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "avatar_part");
    private UUID streamId;
    private byte[] data;
    private boolean isFinal;
    public FSBAvatarPartS2C(UUID streamId, byte[] data, boolean isFinal) {
        this.streamId = streamId;
        this.data = data;
        this.isFinal = isFinal;
    }

    public FSBAvatarPartS2C(IFriendlyByteBuf buf) {
        this.streamId = buf.readUUID();
        this.isFinal = buf.readByte() == 1;
        this.data = buf.readByteArray();
    }

    @Override
    public Identifier getIdentifier() {
        return null;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeUUID(streamId);
        buf.writeByte(isFinal ? 1 : 0);
        buf.writeByteArray(data);
    }

    public byte[] getData() {
        return data;
    }

    public UUID getId() {
        return streamId;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
