package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.util.UUID;

public class FSBFetchAvatarC2S implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "fetch_avatar");

    private UUID avatarOwner;

    public FSBFetchAvatarC2S(UUID avatarOwner) {
        this.avatarOwner = avatarOwner;
    }

    public FSBFetchAvatarC2S(IFriendlyByteBuf buf) {
        this.avatarOwner = buf.readUUID();
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeUUID(avatarOwner);
    }

    public UUID getAvatarOwner() {
        return avatarOwner;
    }
}
