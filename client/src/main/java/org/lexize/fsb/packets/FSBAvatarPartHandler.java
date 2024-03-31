package org.lexize.fsb.packets;

import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.client.FSBAvatarPartS2C;
import org.lexize.fsb.utils.IFriendlyByteBuf;

import java.io.IOException;

public class FSBAvatarPartHandler extends FSBClientPacketHandler<FSBAvatarPartS2C> {
    public FSBAvatarPartHandler(FSBClient parent) {
        super(parent);
    }

    @Override
    public void handle(FSBAvatarPartS2C packet) {
        if (!parent.allowAvatars()) return;
        try {
            parent.acceptAvatarPart(packet.getTarget(), packet.getId(), packet.getData(), packet.isFinal());
        } catch (IOException ignored) {}
    }

    @Override
    public FSBAvatarPartS2C serialize(IFriendlyByteBuf buf) {
        return new FSBAvatarPartS2C(buf);
    }
}
