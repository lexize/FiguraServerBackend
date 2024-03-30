package org.lexize.fsb;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.FriendlyBufWrapper;

import java.nio.file.Path;
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
}
