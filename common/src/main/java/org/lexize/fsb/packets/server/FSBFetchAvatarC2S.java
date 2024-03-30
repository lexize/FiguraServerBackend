package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FSBFetchAvatarC2S implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "fetch_avatar");

    private UUID avatarOwner;
    private String id;
    public FSBFetchAvatarC2S(UUID avatarOwner, String id) {
        this.avatarOwner = avatarOwner;
        this.id = id;
    }

    public FSBFetchAvatarC2S(IFriendlyByteBuf buf) {
        this.avatarOwner = buf.readUUID();
        this.id = new String(buf.readByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeUUID(avatarOwner);
        buf.writeByteArray(id.getBytes(StandardCharsets.UTF_8));
    }

    public UUID getAvatarOwner() {
        return avatarOwner;
    }
}
