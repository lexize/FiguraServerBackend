package org.lexize.fsb;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import org.lexize.fsb.packets.IFSBClientPacketHandler;
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
    }

    private record ClientListener<T extends IFSBPacket>(
            IFSBClientPacketHandler<T> parent) implements ClientPlayNetworking.PlayChannelHandler {

        @Override
            public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
                T packet = parent.serialize(new FriendlyBufWrapper(buf));
                parent.handle(packet);
            }
        }
}
