package org.lexize.fsb;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import org.lexize.fsb.packets.IFSBClientPacketHandler;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.FriendlyBufWrapper;
import org.lexize.fsb.utils.Identifier;

import java.util.Map;

public class FSBFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Registering packet handlers
        for (Map.Entry<Identifier, IFSBClientPacketHandler<?>> entry: FSBClient.CLIENT_HANDLERS.entrySet()) {
            ClientPlayNetworking.registerGlobalReceiver(entry.getKey().toResourceLocation(), new ClientListener<>(entry.getValue()));
        }
    }

    private static class ClientListener<T extends IFSBPacket> implements ClientPlayNetworking.PlayChannelHandler {
        private final IFSBClientPacketHandler<T> handler;

        private ClientListener(IFSBClientPacketHandler<T> handler) {
            this.handler = handler;
        }

        @Override
        public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
            T packet = this.handler.serialize(new FriendlyBufWrapper(buf));
            this.handler.handle(packet);
        }
    }
}
