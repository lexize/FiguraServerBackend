package org.lexize.fsb.packets.client;

import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.backend2.NetworkStuff;
import org.lexize.fsb.FSBClient;
import org.lexize.fsb.utils.IFriendlyByteBuf;

import static org.lexize.fsb.FSBClient.FSBPriority;
import static org.lexize.fsb.FSBClient.FSBPriority.*;

public class FSBClientPingHandler extends FSBClientPacketHandler<FSBPingS2C> {
    public FSBClientPingHandler(FSBClient parent) {
        super(parent);
    }

    @Override
    public void handle(FSBPingS2C packet) {
        FSBPriority priority = FSBClient.getPingsPriority();
        if (priority == FIGURA_ONLY ||
                (priority == FIGURA_THEN_FSB && NetworkStuff.isConnected())) return;
        Avatar avatar = AvatarManager.getLoadedAvatar(packet.getSender());
        avatar.runPing(packet.getId(), packet.getData());
    }

    @Override
    public FSBPingS2C serialize(IFriendlyByteBuf buf) {
        return new FSBPingS2C(buf);
    }
}
