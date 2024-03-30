package org.lexize.fsb.packets;

import com.mojang.datafixers.util.Pair;
import org.figuramc.figura.avatar.UserData;
import org.figuramc.figura.avatar.local.CacheAvatarLoader;
import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.client.FSBUserDataS2C;
import org.lexize.fsb.packets.server.FSBFetchAvatarC2S;
import org.lexize.fsb.utils.IFriendlyByteBuf;

import java.util.BitSet;

public class FSBUserDataHandler extends FSBClientPacketHandler<FSBUserDataS2C> {
    public FSBUserDataHandler(FSBClient parent) {
        super(parent);
    }

    @Override
    public void handle(FSBUserDataS2C packet) {
        UserData userData = FSBClient.getUserData().computeIfAbsent(packet.getOwner(), UserData::new);
        Pair<BitSet, BitSet> badges = new Pair<>(packet.getPrideBadges(), new BitSet());
        userData.loadBadges(badges);
        if (!CacheAvatarLoader.checkAndLoad(packet.getAvatarHash(), userData)) {
            parent.sendC2SPacket(new FSBFetchAvatarC2S(packet.getOwner()));
        }
    }

    @Override
    public FSBUserDataS2C serialize(IFriendlyByteBuf buf) {
        return new FSBUserDataS2C(buf);
    }
}
