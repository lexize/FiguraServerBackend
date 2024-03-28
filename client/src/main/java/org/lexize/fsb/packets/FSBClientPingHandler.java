package org.lexize.fsb.packets;

import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.backend2.NetworkStuff;
import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.client.FSBPingS2C;
import org.lexize.fsb.utils.IFriendlyByteBuf;

public class FSBClientPingHandler implements IFSBClientPacketHandler<FSBPingS2C> {
    @Override
    public void handle(FSBPingS2C packet) {
        int priority = FSBClient.PINGS_PRIORITY.value;
        if (priority == 0 || (priority == 1 && NetworkStuff.isConnected())) return;
        Avatar avatar = AvatarManager.getLoadedAvatar(packet.getSender());
        avatar.runPing(packet.getId(), packet.getData());
    }

    @Override
    public FSBPingS2C serialize(IFriendlyByteBuf buf) {
        return new FSBPingS2C(buf);
    }
}
