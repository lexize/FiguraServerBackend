package org.lexize.fsb.packets.client;

import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.server.FSBHandshakeC2S;
import org.lexize.fsb.utils.IFriendlyByteBuf;

public class FSBClientHandshakeHandler extends FSBClientPacketHandler<FSBHandshakeS2C> {

    public FSBClientHandshakeHandler(FSBClient parent) {
        super(parent);
    }

    @Override
    public void handle(FSBHandshakeS2C packet) {
        parent.onConnectFSB(packet.allowAvatars(), packet.allowPings());
        parent.sendC2SPacket(new FSBHandshakeC2S());
    }

    @Override
    public FSBHandshakeS2C serialize(IFriendlyByteBuf buf) {
        return new FSBHandshakeS2C(buf);
    }
}
