package org.lexize.fsb.packets;

import org.lexize.fsb.utils.IFriendlyByteBuf;
import org.lexize.fsb.utils.Identifier;

// TODO: Replace all string representation of hashes to byte arrays
public interface IFSBPacket {
    Identifier getIdentifier();
    void write(IFriendlyByteBuf buf);
}
