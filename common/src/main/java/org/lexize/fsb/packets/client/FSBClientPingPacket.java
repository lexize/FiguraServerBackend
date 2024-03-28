package org.lexize.fsb.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.lexize.fsb.FSBClient;
import org.lexize.fsb.FSBMod;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

public class FSBClientPingPacket implements IFSBClientPacket {
    public static final Identifier ID = new Identifier(FSBMod.MOD_ID, "ping");

    public FSBClientPingPacket(IFriendlyByteBuf buf) {

    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {

    }

    @Override
    public void handle(FSBClient client) {

    }
}
