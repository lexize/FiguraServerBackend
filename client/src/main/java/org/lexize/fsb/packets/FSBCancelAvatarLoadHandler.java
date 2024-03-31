package org.lexize.fsb.packets;

import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.client.FSBCancelAvatarLoadS2C;
import org.lexize.fsb.utils.IFriendlyByteBuf;

public class FSBCancelAvatarLoadHandler extends FSBClientPacketHandler<FSBCancelAvatarLoadS2C> {

    public FSBCancelAvatarLoadHandler(FSBClient parent) {
        super(parent);
    }

    @Override
    public void handle(FSBCancelAvatarLoadS2C packet) {
        parent.cancelAvatarLoad(packet.getStreamId());
    }

    @Override
    public FSBCancelAvatarLoadS2C serialize(IFriendlyByteBuf buf) {
        return new FSBCancelAvatarLoadS2C(buf);
    }
}
