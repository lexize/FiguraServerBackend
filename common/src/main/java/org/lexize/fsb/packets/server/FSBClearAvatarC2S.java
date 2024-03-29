package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

public class FSBClearAvatarC2S implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "clear_avatar");

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {

    }
}
