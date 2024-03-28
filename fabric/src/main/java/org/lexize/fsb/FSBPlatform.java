package org.lexize.fsb;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.FriendlyBufWrapper;

public class FSBPlatform {
    public static void sendServerPacket(ServerPlayer receiver, IFSBPacket packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.write(new FriendlyBufWrapper(buf));
        ServerPlayNetworking.send(receiver, packet.getIdentifier().toResourceLocation(), buf);
    }
}
