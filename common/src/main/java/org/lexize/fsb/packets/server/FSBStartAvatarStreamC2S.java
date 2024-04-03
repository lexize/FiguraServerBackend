package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FSBStartAvatarStreamC2S implements IFSBPacket {
    public static final Identifier ID = new Identifier(FSB.MOD_ID, "start_avatar_stream");
    public UUID streamUUID;
    public String finalHash;
    public String ehash;
    public String id;

    public FSBStartAvatarStreamC2S(UUID streamUUID, String finalHash, String ehash, String id) {
        this.streamUUID = streamUUID;
        this.finalHash = finalHash;
        this.ehash = ehash;
        this.id = id;
    }

    public FSBStartAvatarStreamC2S(IFriendlyByteBuf buf) {
        this.streamUUID = buf.readUUID();
        this.finalHash = new String(buf.readByteArray(), StandardCharsets.UTF_8);
        this.ehash = new String(buf.readByteArray(), StandardCharsets.UTF_8);
        this.id = new String(buf.readByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeUUID(streamUUID);
        buf.writeByteArray(finalHash.getBytes(StandardCharsets.UTF_8));
        buf.writeByteArray(ehash.getBytes(StandardCharsets.UTF_8));
        buf.writeByteArray(id.getBytes(StandardCharsets.UTF_8));
    }
}
