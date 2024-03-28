package org.lexize.fsb;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.lexize.fsb.packets.IFSBClientPacketHandler;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.packets.client.FSBPingS2C;
import org.lexize.fsb.packets.server.FSBServerPingHandler;
import org.lexize.fsb.packets.server.IFSBServerPacketHandler;
import org.lexize.fsb.utils.Identifier;

import java.util.HashMap;
import java.util.Map;

public class FSBFabric implements ModInitializer {

    private static final HashMap<Identifier, IFSBServerPacketHandler<?>> SERVER_PACKETS = new HashMap<>() {{
       put(FSBPingS2C.ID, new FSBServerPingHandler());
    }};

    @Override
    public void onInitialize() {
        for (Map.Entry<Identifier, IFSBServerPacketHandler<?>> entry: SERVER_PACKETS.entrySet()) {
            ServerPlayNetworking.registerGlobalReceiver(entry.getKey().toResourceLocation(), new ServerListener<>(entry.getValue()));
        }
    }

    private static class ServerListener<T extends IFSBPacket> implements ServerPlayNetworking.PlayChannelHandler {
        private final IFSBServerPacketHandler<T> handler;

        private ServerListener(IFSBServerPacketHandler<T> handler) {
            this.handler = handler;
        }
        @Override
        public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
            T packet = this.handler.serialize(buf);
            this.handler.handle(server, player, packet);
        }
    }
}
