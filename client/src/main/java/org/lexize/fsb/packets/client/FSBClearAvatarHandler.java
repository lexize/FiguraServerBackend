package org.lexize.fsb.packets.client;

import org.figuramc.figura.avatar.AvatarManager;
import org.lexize.fsb.FSBClient;
import org.lexize.fsb.utils.IFriendlyByteBuf;

public class FSBClearAvatarHandler extends FSBClientPacketHandler<FSBClearAvatarS2C> {
    public FSBClearAvatarHandler(FSBClient parent) {
        super(parent);
    }

    @Override
    public void handle(FSBClearAvatarS2C packet) {
        if (!parent.allowAvatars()) return;
        AvatarManager.clearAvatars(packet.getOwner());
    }

    @Override
    public FSBClearAvatarS2C serialize(IFriendlyByteBuf buf) {
        return new FSBClearAvatarS2C(buf);
    }
}
