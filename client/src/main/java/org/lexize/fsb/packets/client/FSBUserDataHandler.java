package org.lexize.fsb.packets.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import org.figuramc.figura.avatar.UserData;
import org.figuramc.figura.avatar.local.CacheAvatarLoader;
import org.figuramc.figura.gui.FiguraToast;
import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.server.FSBFetchAvatarC2S;
import org.lexize.fsb.utils.IFriendlyByteBuf;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.UUID;

public class FSBUserDataHandler extends FSBClientPacketHandler<FSBUserDataS2C> {
    public FSBUserDataHandler(FSBClient parent) {
        super(parent);
    }

    @Override
    public void handle(FSBUserDataS2C packet) {
        if (!parent.allowAvatars()) return;
        UserData userData = FSBClient.getUserData().computeIfAbsent(packet.getTarget(), UserData::new);
        Pair<BitSet, BitSet> badges = new Pair<>(packet.getPrideBadges(), new BitSet());
        userData.loadBadges(badges);
        ArrayList<String> invalidEhashes = new ArrayList<>();
        for (var entry: packet.getAvatarHashes().entrySet()) {
            if (parent.isUUIDLocal(packet.getTarget()) && !parent.checkEHash(entry.getValue().left(), entry.getValue().right())) {
                invalidEhashes.add(entry.getKey());
                continue;
            }
            if (!CacheAvatarLoader.checkAndLoad(entry.getValue().left(), userData)) {
                UUID streamId = UUID.randomUUID();
                if (!parent.expectingHash(entry.getValue().left())) {
                    parent.prepareStream(streamId, entry.getValue().left());
                    parent.expectHash(entry.getValue().left());
                    parent.sendC2SPacket(new FSBFetchAvatarC2S(entry.getValue().left(), streamId));
                }
                parent.addToFetchConsumers(entry.getValue().left(), packet.getTarget());
            }
        }
        if (!invalidEhashes.isEmpty()) {
            String s = String.join(", ", invalidEhashes);
            FiguraToast.sendToast(
                    Component.translatable("fsb.security.wrong_ehash"),
                    Component.translatable("fsb.security.wrong_ehash.desc", s),
                    FiguraToast.ToastType.ERROR
            );
        }
    }

    @Override
    public FSBUserDataS2C serialize(IFriendlyByteBuf buf) {
        return new FSBUserDataS2C(buf);
    }
}
