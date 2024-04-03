package org.lexize.fsb;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.lexize.fsb.packets.client.FSBClientPacketHandler;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.FriendlyBufWrapper;

import static org.lexize.fsb.utils.FabricUtils.fromId;

public class FSBClientFabric extends FSBClient implements ClientModInitializer {
    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void sendC2SPacket(IFSBPacket packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.write(new FriendlyBufWrapper(buf));
        ClientPlayNetworking.send(fromId(packet.getIdentifier()), buf);
    }

    @Override
    public void initializeClientPackets() {
        for (var entry: CLIENT_HANDLERS.entrySet()) {
            ClientPlayNetworking.registerGlobalReceiver(fromId(entry.getKey()), new ClientListener<>(entry.getValue()));
        }
    }

    @Override
    public void onInitializeClient() {
        initializeClientPackets();
        ClientPlayConnectionEvents.JOIN.register(
                new ResourceLocation(FSB.MOD_ID, "player_join_listener"),
                this::onPlayConnect
                );
        ClientPlayConnectionEvents.DISCONNECT.register(
                new ResourceLocation(FSB.MOD_ID, "player_join_listener"),
                this::onPlayDisconnect
        );
    }

    private void onPlayConnect(ClientPacketListener handler, PacketSender sender, Minecraft client) {
        onConnectServer(handler.getConnection().getRemoteAddress());
    }

    private void onPlayDisconnect(ClientPacketListener clientPacketListener, Minecraft minecraft) {
        onDisconnect();
    }

    private record ClientListener<T extends IFSBPacket>(
            FSBClientPacketHandler<T> parent) implements ClientPlayNetworking.PlayChannelHandler {

        @Override
            public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
                T packet = parent.serialize(new FriendlyBufWrapper(buf));
                parent.handle(packet);
            }
        }
}
