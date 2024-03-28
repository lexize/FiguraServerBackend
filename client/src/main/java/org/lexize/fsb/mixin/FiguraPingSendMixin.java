package org.lexize.fsb.mixin;

import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.backend2.NetworkStuff;
import org.lexize.fsb.FSBClient;
import org.lexize.fsb.FSBClientPlatform;
import org.lexize.fsb.FSBPlatform;
import org.lexize.fsb.packets.server.FSBPingC2S;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkStuff.class)
public class FiguraPingSendMixin {
    @Inject(method = "sendPing", at = @At("HEAD"), cancellable = true)
    private static void onSendPing(int id, boolean sync, byte[] data, CallbackInfo ci) {
        if (!AvatarManager.localUploaded) return;
        int ping_priority = FSBClient.PINGS_PRIORITY.value;

        boolean send = FSBClient.isConnected() && switch (ping_priority) {
            case 0 -> false;
            case 1 -> !NetworkStuff.isConnected();
            default -> true;
        };

        boolean allow_figura_ping = NetworkStuff.isConnected() && switch (ping_priority) {
            default -> true;
            case 4 -> !FSBClient.isConnected();
            case 5 -> false;
        };

        if (send) {
            FSBPingC2S packet = new FSBPingC2S(id, sync, data);
            FSBClientPlatform.sendC2SPacket(packet);
        }
    }
}
