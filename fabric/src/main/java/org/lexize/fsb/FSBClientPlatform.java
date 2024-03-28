package org.lexize.fsb;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.FriendlyBufWrapper;
public class FSBClientPlatform {
    public static void sendC2SPacket(IFSBPacket packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.write(new FriendlyBufWrapper(buf));
        ClientPlayNetworking.send(packet.getIdentifier().toResourceLocation(), buf);
    }
}