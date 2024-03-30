package org.lexize.fsb.packets;

import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.client.FSBAvatarPartS2C;
import org.lexize.fsb.utils.IFriendlyByteBuf;

import java.io.IOException;

public class FSBAvatarPartHandler implements IFSBClientPacketHandler<FSBAvatarPartS2C> {
    @Override
    public void handle(FSBAvatarPartS2C packet) {
        try {
            FSBClient.instance().acceptAvatarPart(packet.getOwner(), packet.getData(), packet.isFinal(), packet.getHash());
        } catch (IOException ignored) {}
    }

    @Override
    public FSBAvatarPartS2C serialize(IFriendlyByteBuf buf) {
        return new FSBAvatarPartS2C(buf);
    }
}