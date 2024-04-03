package org.lexize.fsb.packets.client;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;
import org.lexize.fsb.utils.Pair;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.HashMap;
import java.util.UUID;

public class FSBUserDataS2C implements IFSBPacket {
    public static Identifier ID = new Identifier(FSB.MOD_ID, "user_data");
    private UUID owner;
    private BitSet prideBadges;
    private HashMap<String, Pair<String, String>> avatarHashes;

    public FSBUserDataS2C(UUID owner, BitSet prideBadges, HashMap<String, Pair<String, String>> avatarHashes) {
        this.owner = owner;
        this.prideBadges = prideBadges;
        this.avatarHashes = avatarHashes;
    }

    public FSBUserDataS2C(IFriendlyByteBuf buf) {
        owner = buf.readUUID();
        prideBadges = BitSet.valueOf(buf.readByteArray());
        int hashesCount = buf.readVarInt();
        avatarHashes = new HashMap<>();
        for (int i = 0; i < hashesCount; i++) {
            avatarHashes.put(
                    new String(buf.readByteArray(), StandardCharsets.UTF_8),
                    new Pair<>(
                            new String(buf.readByteArray(), StandardCharsets.UTF_8),
                            new String(buf.readByteArray(), StandardCharsets.UTF_8)));
        }
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeUUID(owner);
        buf.writeVarInt(avatarHashes.size());
        for(var entry: avatarHashes.entrySet()) {
            buf.writeByteArray(entry.getKey().getBytes(StandardCharsets.UTF_8));
            buf.writeByteArray(entry.getValue().left().getBytes(StandardCharsets.UTF_8));
            buf.writeByteArray(entry.getValue().right().getBytes(StandardCharsets.UTF_8));
        }
        buf.writeByteArray(prideBadges.toByteArray());
    }

    public UUID getTarget() {
        return owner;
    }

    public BitSet getPrideBadges() {
        return prideBadges;
    }

    public HashMap<String, Pair<String, String>> getAvatarHashes() {
        return avatarHashes;
    }
}
