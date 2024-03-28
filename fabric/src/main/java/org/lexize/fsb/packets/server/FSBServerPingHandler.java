package org.lexize.fsb.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.lexize.fsb.FSBPlatform;
import org.lexize.fsb.packets.client.FSBPingS2C;
import org.lexize.fsb.utils.FriendlyBufWrapper;

public class FSBServerPingHandler implements IFSBServerPacketHandler<FSBPingC2S> {
    @Override
    public void handle(MinecraftServer server, ServerPlayer sender, FSBPingC2S packet) {
        FSBPingS2C s2cPacket = new FSBPingS2C(sender.getUUID(), packet.getId(), packet.getData());
        if (packet.shouldSync()) {
            FSBPlatform.sendServerPacket(sender, s2cPacket);
        }
    }

    @Override
    public FSBPingC2S serialize(FriendlyByteBuf buf) {
        return new FSBPingC2S(new FriendlyBufWrapper(buf));
    }
}
