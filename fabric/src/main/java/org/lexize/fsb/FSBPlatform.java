package org.lexize.fsb;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.lexize.fsb.packets.client.IFSBClientPacket;
import org.lexize.fsb.packets.client.FSBClientPingPacket;
import org.lexize.fsb.packets.server.IFSBServerPacket;
import org.lexize.fsb.packets.server.FSBServerPingPacket;
import org.lexize.fsb.utils.FriendlyBufWrapper;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;

public class FSBPlatform implements IFSBPlatform {

    private static final HashMap<Identifier, Class<? extends IFSBClientPacket>> CLIENT_PACKETS = new HashMap<>() {{
        put(FSBClientPingPacket.ID, FSBClientPingPacket.class);
    }};

    private static final HashMap<Identifier, Class<? extends IFSBServerPacket>> SERVER_PACKETS = new HashMap<>() {{
        put(FSBServerPingPacket.ID, FSBServerPingPacket.class);
    }};

    private FSBClient client;
    private FSBServer server;

    @Override
    public void initClientPackets(FSBClient client) {
        this.client = client;
        for (var entry: CLIENT_PACKETS.entrySet()) {
            ClientPlayNetworking.registerGlobalReceiver(entry.getKey().toResourceLocation(), new ClientListener(this, entry.getValue()));
        }
    }

    @Override
    public void initServerPackets(FSBServer server) {
        this.server = server;
        for (var entry: SERVER_PACKETS.entrySet()) {
            ServerPlayNetworking.registerGlobalReceiver(entry.getKey().toResourceLocation(), new ServerListener(this, entry.getValue()));
        }
    }

    @Override
    public Path getConfigDir() {
        return null;
    }

    private static class ClientListener implements ClientPlayNetworking.PlayChannelHandler {
        private final Class<? extends IFSBClientPacket> packetClass;
        private final FSBPlatform parent;

        ClientListener(FSBPlatform parent, Class<? extends IFSBClientPacket> packetClass) {
            this.parent = parent;
            this.packetClass = packetClass;
        }

        @Override
        public void receive(Minecraft mc, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
            try {
                Constructor<? extends IFSBClientPacket> constructor = packetClass.getDeclaredConstructor(IFriendlyByteBuf.class);
                IFSBClientPacket packet = constructor.newInstance(new FriendlyBufWrapper(buf));
                packet.handle(parent.client);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ServerListener implements ServerPlayNetworking.PlayChannelHandler {
        private final Class<? extends IFSBServerPacket> packetClass;
        private final FSBPlatform parent;

        ServerListener(FSBPlatform parent, Class<? extends IFSBServerPacket> packetClass) {
            this.parent = parent;
            this.packetClass = packetClass;
        }

        @Override
        public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
            try {
                Constructor<? extends IFSBServerPacket> constructor = packetClass.getDeclaredConstructor(IFriendlyByteBuf.class);
                IFSBServerPacket packet = constructor.newInstance(new FriendlyBufWrapper(buf));
                packet.handle(parent.server, player.getUUID());
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
