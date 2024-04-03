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
    private UUID streamID;
    private boolean isFinal;

    public FSBAvatarPartC2S(byte[] data, UUID streamID, boolean isFinal) {
        this.data = data;
        this.streamID = streamID;
        this.isFinal = isFinal;
    }

    public FSBAvatarPartC2S(IFriendlyByteBuf buf) {
        this.streamID = buf.readUUID();
        this.isFinal = buf.readByte() == 1;
        this.data = buf.readByteArray();
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeUUID(streamID);
        buf.writeByte(isFinal ? 1 : 0);
        buf.writeByteArray(data);
    }

    public boolean isFinal() {
        return isFinal;
    }

    public byte[] getData() {
        return data;
    }

    public UUID getStreamID() {
        return streamID;
    }
}
