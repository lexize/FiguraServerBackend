package org.lexize.fsb.packets.client;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

public class FSBHandshakeS2C implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "handshake");
    private boolean allowAvatars;
    private boolean allowPings;

    public FSBHandshakeS2C(IFriendlyByteBuf buf) {
        int b = buf.readByte() & 0xFF;
        allowAvatars = (b & 0b1) == 0b1;
        allowPings = (b & 0b10) == 0b10;
    }

    public FSBHandshakeS2C(boolean allowAvatar, boolean allowPings) {
        this.allowAvatars = allowAvatar;
        this.allowPings = allowPings;
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeByte((allowAvatars ? 0b1 : 0) | (allowPings ? 0b10 : 0));
    }

    public boolean allowAvatars() {
        return allowAvatars;
    }

    public boolean allowPings() {
        return allowPings;
    }
}
