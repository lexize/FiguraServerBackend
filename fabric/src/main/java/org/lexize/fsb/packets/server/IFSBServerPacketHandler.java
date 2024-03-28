package org.lexize.fsb.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.lexize.fsb.packets.IFSBPacket;

public interface IFSBServerPacketHandler<T extends IFSBPacket> {
    void handle(MinecraftServer server, ServerPlayer sender, T packet);
    T serialize(FriendlyByteBuf buf);
}
