package org.lexize.fsb.packets.client;

import org.lexize.fsb.FSB;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

import java.nio.charset.StandardCharsets;

public class FSBNotifyS2C implements IFSBPacket {
    public static final Identifier ID = new Identifier(FSB.MOD_ID, "notify");
    private String title;
    private String message;
    private NotificationType type;

    public FSBNotifyS2C(IFriendlyByteBuf buf) {
        type = NotificationType.byOrdinal(buf.readByte());
        title = new String(buf.readByteArray(), StandardCharsets.UTF_8);
        if (buf.readByte() == 1) {
            message = new String(buf.readByteArray(), StandardCharsets.UTF_8);
        }
    }

    public FSBNotifyS2C(String title, String message, NotificationType type) {
        this.title = title;
        this.message = message;
        this.type = type;
    }

    public FSBNotifyS2C(String title, NotificationType type) {
        this.title = title;
        this.type = type;
    }

    @Override
    public Identifier getIdentifier() {
        return ID;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeByte(type.ordinal());
        buf.writeByteArray(title.getBytes(StandardCharsets.UTF_8));
        buf.writeByte(message == null ? 0 : 1);
        if (message != null) {
            buf.writeByteArray(message.getBytes(StandardCharsets.UTF_8));
        }
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public enum NotificationType {
        INFO,
        WARNING,
        ERROR;

        public static NotificationType byOrdinal(int ord) {
            for (NotificationType type : values()) {
                if (type.ordinal() == ord) return type;
            }
            return null;
        }
    }
}
