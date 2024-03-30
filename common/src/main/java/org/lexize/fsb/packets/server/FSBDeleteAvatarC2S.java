package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.nio.charset.StandardCharsets;

public class FSBDeleteAvatarC2S implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "clear_avatar");

    private String id;

    public FSBDeleteAvatarC2S(String avatarID) {
        this.id = avatarID;
    }

    public FSBDeleteAvatarC2S(IFriendlyByteBuf buf) {
        this.id = new String(buf.readByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeByteArray(id.getBytes(StandardCharsets.UTF_8));
    }
}
