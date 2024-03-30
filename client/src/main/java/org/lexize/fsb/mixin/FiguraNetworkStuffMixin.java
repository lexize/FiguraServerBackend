package org.lexize.fsb.mixin;

import net.minecraft.nbt.NbtIo;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.avatar.UserData;
import org.figuramc.figura.backend2.NetworkStuff;
import org.figuramc.figura.backend2.websocket.C2SMessageHandler;
import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.server.FSBAvatarPartC2S;
import org.lexize.fsb.packets.server.FSBDeleteAvatarC2S;
import org.lexize.fsb.packets.server.FSBFetchUserDataC2S;
import org.lexize.fsb.packets.server.FSBPingC2S;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lexize.fsb.FSBClient.FSBPriority;

@Mixin(NetworkStuff.class)
public class FiguraNetworkStuffMixin {
    @Inject(method = "sendPing", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onSendPing(int id, boolean sync, byte[] data, CallbackInfo ci) {
        if (!AvatarManager.localUploaded) return;
        FSBPriority priority = FSBClient.getPingsPriority();

        boolean send = FSBClient.instance().allowPings() && switch (priority) {
            case FIGURA_ONLY -> false;
            case FIGURA_THEN_FSB -> !NetworkStuff.isConnected();
            default -> true;
        };

        boolean cancel_original_ping = !NetworkStuff.isConnected() && switch (priority) {
            case FSB_ONLY -> true;
            case FSB_THEN_FIGURA -> !FSBClient.instance().allowPings();
            default -> false;
        };

        if (cancel_original_ping) {
            ci.cancel();
        }

        if (send) {
            sync = sync && switch (FSBClient.getSyncPriority()) {
                case FIGURA_ONLY -> false;
                case FIGURA_THEN_FSB -> !NetworkStuff.isConnected();
                case FSB_THEN_FIGURA, FSB_ONLY -> true;
            };

            FSBPingC2S packet = new FSBPingC2S(id, sync, data);
            FSBClient.instance().sendC2SPacket(packet);
        }
    }

    @Inject(
            at = @At(value = "HEAD"),
            method = "uploadAvatar", cancellable = true)
    private static void onAvatarUpload(Avatar avatar, CallbackInfo ci) throws IOException {
        if (avatar == null || avatar.nbt == null)
            return;
        FSBPriority priority = FSBClient.getAvatarsPriority();
        boolean send = FSBClient.instance().allowAvatars() && switch (priority) {
            case FIGURA_ONLY -> false;
            case FIGURA_THEN_FSB -> !NetworkStuff.isConnected();
            default -> true;
        };
        if (send) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            NbtIo.writeCompressed(avatar.nbt, baos);
            FSBAvatarPartC2S packet = new FSBAvatarPartC2S(
                    true,
                    baos.toByteArray(),
                    avatar.id == null ? "avatar" : avatar.id);
            FSBClient.instance().sendC2SPacket(packet);
            baos.close();
            ci.cancel();
        }
    }

    @Inject( at = @At(value = "HEAD"), method = "deleteAvatar", cancellable = true)
    private static void onAvatarDelete(String avatar, CallbackInfo ci) {
        FSBPriority priority = FSBClient.getAvatarsPriority();
        boolean send = FSBClient.instance().allowAvatars() && switch (priority) {
            case FIGURA_ONLY -> false;
            case FIGURA_THEN_FSB -> !NetworkStuff.isConnected();
            default -> true;
        };

        if (send) {
            FSBClient.instance().sendC2SPacket(new FSBDeleteAvatarC2S(avatar));
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getUser", cancellable = true)
    private static void onUserFetch(UserData user, CallbackInfo ci) {
        FSBPriority priority = FSBClient.getAvatarsPriority();
        boolean send = FSBClient.instance().allowAvatars() && switch (priority) {
            case FIGURA_ONLY -> false;
            case FIGURA_THEN_FSB -> !NetworkStuff.isConnected();
            default -> true;
        };

        if (send) {
            FSBClient.instance().sendC2SPacket(new FSBFetchUserDataC2S(user.id));
            ci.cancel();
        }
    }

    @Redirect(method = "sendPing", at = @At(
            value = "INVOKE",
            target = "Lorg/figuramc/figura/backend2/websocket/C2SMessageHandler;ping(IZ[B)Ljava/nio/ByteBuffer;"),
            remap = false)
    private static ByteBuffer onPingSync(int id, boolean sync, byte[] data) throws IOException {
        sync = sync && switch (FSBClient.getSyncPriority()) {
            case FIGURA_ONLY, FIGURA_THEN_FSB -> true; // In case if this method is called Figura backend is always connected
            case FSB_THEN_FIGURA -> !FSBClient.instance().allowPings(); // True only if FSB is disconnected
            case FSB_ONLY -> false;
        };
        return C2SMessageHandler.ping(id, sync, data);
    }
}
