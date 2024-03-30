package org.lexize.fsb.packets.client;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;

public class FSBUserDataS2C implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "user_data");
    private UUID owner;
    private BitSet prideBadges;
    private String avatarHash;

    public FSBUserDataS2C(UUID owner, BitSet prideBadges, String avatarHash) {
        this.owner = owner;
        this.prideBadges = prideBadges;
        this.avatarHash = avatarHash;
    }

    public FSBUserDataS2C(IFriendlyByteBuf buf) {
        owner = buf.readUUID();
        prideBadges = BitSet.valueOf(buf.readByteArray());
        avatarHash = new String(buf.readByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeUUID(owner);
        buf.writeByteArray(avatarHash.getBytes(StandardCharsets.UTF_8));
        buf.writeByteArray(prideBadges.toByteArray());
    }

    public UUID getOwner() {
        return owner;
    }

    public BitSet getPrideBadges() {
        return prideBadges;
    }

    public String getAvatarHash() {
        return avatarHash;
    }
}
