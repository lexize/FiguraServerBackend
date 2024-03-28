package org.lexize.fsb.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.lexize.fsb.FSBMod;
import org.lexize.fsb.FSBServer;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.util.UUID;

public class FSBServerPingPacket implements IFSBServerPacket {
    public static final Identifier ID = new Identifier(FSBMod.MOD_ID, "ping");

    public FSBServerPingPacket(IFriendlyByteBuf buf) {

    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {

    }

    @Override
    public void handle(FSBServer server, UUID sender) {

    }
}
