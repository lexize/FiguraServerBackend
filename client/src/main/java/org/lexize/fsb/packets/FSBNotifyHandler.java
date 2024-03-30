package org.lexize.fsb.packets;

import org.figuramc.figura.gui.FiguraToast;
import org.lexize.fsb.FSBClient;
import org.lexize.fsb.packets.client.FSBNotifyS2C;
import org.lexize.fsb.utils.IFriendlyByteBuf;

public class FSBNotifyHandler extends FSBClientPacketHandler<FSBNotifyS2C> {
    public FSBNotifyHandler(FSBClient parent) {
        super(parent);
    }

    @Override
    public void handle(FSBNotifyS2C packet) {
        FiguraToast.ToastType type = switch (packet.getType()) {
            case INFO -> FiguraToast.ToastType.DEFAULT;
            case WARNING -> FiguraToast.ToastType.WARNING;
            case ERROR -> FiguraToast.ToastType.ERROR;
        };
        if (packet.getMessage() == null) FiguraToast.sendToast(packet.getTitle(), type);
        else FiguraToast.sendToast(packet.getTitle(), packet.getMessage(), type);
    }

    @Override
    public FSBNotifyS2C serialize(IFriendlyByteBuf buf) {
        return new FSBNotifyS2C(buf);
    }
}
