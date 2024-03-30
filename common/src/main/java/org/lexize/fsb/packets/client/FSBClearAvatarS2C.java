package org.lexize.fsb.packets.client;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.util.UUID;

public class FSBClearAvatarS2C implements IFSBPacket {
    public static final Identifier ID = new Identifier(FSB.MOD_ID, "clear_avatar");

    private UUID owner;

    public FSBClearAvatarS2C(UUID owner) {
        this.owner = owner;
    }

    public FSBClearAvatarS2C(IFriendlyByteBuf buf) {
        this.owner = buf.readUUID();
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeUUID(owner);
    }

    public UUID getOwner() {
        return owner;
    }
}
