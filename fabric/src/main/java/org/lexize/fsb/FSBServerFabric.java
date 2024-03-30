package org.lexize.fsb;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.packets.server.FSBServerPacketHandler;
import org.lexize.fsb.utils.FriendlyBufWrapper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.lexize.fsb.utils.FabricUtils.fromId;

public class FSBServerFabric extends FSBServer implements DedicatedServerModInitializer {
    private MinecraftServer serverInstance;
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(s -> {
            serverInstance = s;
        });
        initialize();
    }

    @Override
    protected void initializeServerPackets() {
        for (var entry: SERVER_PACKET_HANDLERS.entrySet()) {
            ServerPlayNetworking.registerGlobalReceiver(fromId(entry.getKey()), new ServerListener<>(entry.getValue()));
        }
    }

    @Override
    public void sendS2CPacketPlatform(UUID receiver, IFSBPacket packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.write(new FriendlyBufWrapper(buf));
        ServerPlayNetworking.send(
                serverInstance.getPlayerList().getPlayer(receiver),
                fromId(packet.getIdentifier()), buf);
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public List<UUID> getPlayers() {
        ArrayList<UUID> players = new ArrayList<>();
        for (ServerPlayer pl : serverInstance.getPlayerList().getPlayers()) {
            players.add(pl.getUUID());
        }
        return players;
    }

    private static class ServerListener<T extends IFSBPacket> implements ServerPlayNetworking.PlayChannelHandler {
        private final FSBServerPacketHandler<T> handler;

        private ServerListener(FSBServerPacketHandler<T> handler) {
            this.handler = handler;
        }

        @Override
        public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl l, FriendlyByteBuf buf, PacketSender responseSender) {
            T packet = handler.serialize(new FriendlyBufWrapper(buf));
            handler.handle(player.getUUID(), packet);
        }
    }
}
