package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.util.UUID;

public class FSBFetchUserDataC2S implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "fetch_user_data");
    private UUID user;

    public FSBFetchUserDataC2S(IFriendlyByteBuf buf) {
        user = buf.readUUID();
    }

    public FSBFetchUserDataC2S(UUID user) {
        this.user = user;
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeUUID(user);
    }

    public UUID getUser() {
        return user;
    }
}
