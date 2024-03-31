package org.lexize.fsb.packets.client;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.util.UUID;

public class FSBCancelAvatarLoadS2C implements IFSBPacket {
    public static final Identifier ID = new Identifier(FSB.MOD_ID, "cancel_avatar_load");

    private UUID streamID;

    public FSBCancelAvatarLoadS2C(UUID streamID) {
        this.streamID = streamID;
    }

    public FSBCancelAvatarLoadS2C(IFriendlyByteBuf buf) {
        this.streamID = buf.readUUID();
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeUUID(streamID);
    }

    public UUID getStreamId() {
        return streamID;
    }
}
