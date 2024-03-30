package org.lexize.fsb.packets;

import org.figuramc.figura.avatar.AvatarManager;
import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.client.FSBClearAvatarS2C;
import org.lexize.fsb.utils.IFriendlyByteBuf;

public class FSBClearAvatarHandler extends FSBClientPacketHandler<FSBClearAvatarS2C> {
    public FSBClearAvatarHandler(FSBClient parent) {
        super(parent);
    }

    @Override
    public void handle(FSBClearAvatarS2C packet) {
        AvatarManager.clearAvatars(packet.getOwner());
    }

    @Override
    public FSBClearAvatarS2C serialize(IFriendlyByteBuf buf) {
        return new FSBClearAvatarS2C(buf);
    }
}
