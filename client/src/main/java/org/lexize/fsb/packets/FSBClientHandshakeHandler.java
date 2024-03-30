package org.lexize.fsb.packets;

import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.client.FSBHandshakeS2C;
import org.lexize.fsb.packets.server.FSBHandshakeC2S;
import org.lexize.fsb.packets.server.FSBPingC2S;
import org.lexize.fsb.utils.IFriendlyByteBuf;

public class FSBClientHandshakeHandler implements IFSBClientPacketHandler<FSBHandshakeS2C>{

    public FSBClientHandshakeHandler() {
    }

    @Override
    public void handle(FSBHandshakeS2C packet) {
        FSBClient.instance().onConnect(packet.allowAvatars(), packet.allowPings());
        FSBClient.instance().sendC2SPacket(new FSBHandshakeC2S());
    }

    @Override
    public FSBHandshakeS2C serialize(IFriendlyByteBuf buf) {
        return new FSBHandshakeS2C(buf);
    }
}
