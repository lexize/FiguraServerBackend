package org.lexize.fsb.mixin;

import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.backend2.NetworkStuff;
import org.figuramc.figura.backend2.websocket.C2SMessageHandler;
import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.server.FSBPingC2S;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lexize.fsb.FSBClient.FSBPriority;

@Mixin(NetworkStuff.class)
public class FiguraPingSendMixin {
    @Inject(method = "sendPing", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onSendPing(int id, boolean sync, byte[] data, CallbackInfo ci) {
        if (!AvatarManager.localUploaded) return;
        FSBPriority priority = FSBClient.getPingsPriority();

        boolean send = FSBClient.instance().isConnected() && switch (priority) {
            case FIGURA_ONLY -> false;
            case FIGURA_THEN_FSB -> !NetworkStuff.isConnected();
            default -> true;
        };

        boolean cancel_figura_ping = !NetworkStuff.isConnected() || switch (priority) {
            default -> false;
            case FSB_THEN_FIGURA -> FSBClient.instance().isConnected();
            case FSB_ONLY -> true;
        };

        if (send) {
            sync = sync && switch (FSBClient.getSyncPriority()) {
                case FIGURA_ONLY -> false;
                case FIGURA_THEN_FSB -> !NetworkStuff.isConnected();
                case FIGURA_AND_FSB -> throw new AssertionError();
                case FSB_THEN_FIGURA, FSB_ONLY -> true;
            };

            FSBPingC2S packet = new FSBPingC2S(id, sync, data);
            FSBClient.instance().sendC2SPacket(packet);
        }

        if (cancel_figura_ping) ci.cancel();
    }

    @Redirect(method = "sendPing", at = @At(
            value = "INVOKE",
            target = "Lorg/figuramc/figura/backend2/websocket/C2SMessageHandler;ping(IZ[B)Ljava/nio/ByteBuffer;"),
            remap = false)
    private static ByteBuffer onPingSync(int id, boolean sync, byte[] data) throws IOException {
        sync = sync && switch (FSBClient.getSyncPriority()) {
            case FIGURA_ONLY, FIGURA_THEN_FSB -> true; // In case if this method is called Figura backend is always connected
            case FIGURA_AND_FSB -> throw new AssertionError(); // Cant be used for Sync
            case FSB_THEN_FIGURA -> !FSBClient.instance().isConnected(); // True only if FSB is disconnected
            case FSB_ONLY -> false;
        };
        return C2SMessageHandler.ping(id, sync, data);
    }
}
