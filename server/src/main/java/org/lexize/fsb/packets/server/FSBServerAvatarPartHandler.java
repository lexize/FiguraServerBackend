package org.lexize.fsb.packets.server;

import org.lexize.fsb.FSBServer;
import org.lexize.fsb.packets.client.FSBNotifyS2C;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class FSBServerAvatarPartHandler extends FSBServerPacketHandler<FSBAvatarPartC2S> {
    protected FSBServerAvatarPartHandler(FSBServer parent) {
        super(parent);
    }

    @Override
    public void handle(UUID player, FSBAvatarPartC2S packet) {
        if (!parent.allowAvatars()) return;
        try {
            String uid = Utils.uuidToHex(player);
            int count = parent.getDatabase().ownedAvatarsCount(uid);
            if (count >= parent.getConfig().getMaxAvatars() && !parent.getDatabase().avatarExists(
                    uid, packet.getId()
            )) {
                parent.sendS2CPacket(player,
                        new FSBNotifyS2C(parent.getConfig().getAvatarCountLimitMessage(), FSBNotifyS2C.NotificationType.ERROR)
                );
                return;
            }
            parent.acceptAvatarPart(player, packet.getId(), packet.getData(), packet.isFinal());
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FSBAvatarPartC2S serialize(IFriendlyByteBuf buf) {
        return new FSBAvatarPartC2S(buf);
    }
}
