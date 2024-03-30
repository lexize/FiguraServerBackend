package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSBServer;
import org.lexize.fsb.packets.client.FSBNotifyS2C;
import org.lexize.fsb.packets.client.FSBPingS2C;
import org.lexize.fsb.utils.IFriendlyByteBuf;

import java.util.UUID;

public class FSBServerPingHandler extends FSBServerPacketHandler<FSBPingC2S>{
    protected FSBServerPingHandler(FSBServer parent) {
        super(parent);
    }

    @Override
    public void handle(UUID player, FSBPingC2S packet) {
        if (!parent.allowPings()) return;
        FSBServer.PingCounter counter = parent.getCounter(player);
        if (counter.getCount() > parent.getConfig().getPingsRateLimit()) {
            parent.sendS2CPacket(player,
                    new FSBNotifyS2C(parent.getConfig().getPingRateLimitMessage(),
                            FSBNotifyS2C.NotificationType.ERROR));
            return;
        }
        if (packet.getData().length + counter.getSize() > parent.getConfig().getPingsSizeLimit()) {
            parent.sendS2CPacket(player,
                    new FSBNotifyS2C(parent.getConfig().getPingSizeLimitMessage(),
                            FSBNotifyS2C.NotificationType.ERROR));
            return;
        }
        for (UUID p: parent.getPlayers()) {
            if (!(p.equals(player) && packet.shouldSync())) {
                parent.sendS2CPacket(p, new FSBPingS2C(player, packet.getId(), packet.getData()));
            }
        }
        counter.addSize(packet.getData().length);
        counter.incCount();
    }

    @Override
    public FSBPingC2S serialize(IFriendlyByteBuf buf) {
        return new FSBPingC2S(buf);
    }
}
