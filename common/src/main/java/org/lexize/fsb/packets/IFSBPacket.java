package org.lexize.fsb.packets;

import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

public interface IFSBPacket {
    Identifier getIdentifier();
    void write(IFriendlyByteBuf buf);
}
