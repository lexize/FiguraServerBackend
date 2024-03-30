package org.lexize.fsb.packets;

import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.client.FSBCancelAvatarLoadS2C;
import org.lexize.fsb.utils.IFriendlyByteBuf;

public class FSBCancelAvatarLoadHandler implements IFSBClientPacketHandler<FSBCancelAvatarLoadS2C> {
    @Override
    public void handle(FSBCancelAvatarLoadS2C packet) {
        FSBClient.instance().cancelAvatarLoad(packet.getOwner());
    }

    @Override
    public FSBCancelAvatarLoadS2C serialize(IFriendlyByteBuf buf) {
        return new FSBCancelAvatarLoadS2C(buf);
    }
}
