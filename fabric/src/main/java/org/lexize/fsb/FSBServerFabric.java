package org.lexize.fsb;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.lexize.fsb.packets.IFSBPacket;

import java.nio.file.Path;
import java.util.UUID;

public class FSBServerFabric extends FSBServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        initializeServerPackets();
    }

    @Override
    protected void initializeServerPackets() {

    }

    @Override
    public void sendS2CPacket(UUID receiver, IFSBPacket packet) {

    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
