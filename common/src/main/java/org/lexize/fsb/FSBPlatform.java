package org.lexize.fsb;

import net.minecraft.server.level.ServerPlayer;
import org.lexize.fsb.packets.IFSBPacket;

public class FSBPlatform {
    public static void sendServerPacket(ServerPlayer receiver, IFSBPacket packet) {
        throw new AssertionError();
    }
}