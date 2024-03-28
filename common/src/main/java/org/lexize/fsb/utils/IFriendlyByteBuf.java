package org.lexize.fsb.utils;

import java.util.UUID;

/**
 * Platform specific FriendlyByteBuf implementation.
 */
public interface IFriendlyByteBuf {
    IFriendlyByteBuf writeByte(int b);
    IFriendlyByteBuf writeShort(int s);
    IFriendlyByteBuf writeInt(int i);
    IFriendlyByteBuf writeVarInt(int i);
    IFriendlyByteBuf writeLong(long l);
    IFriendlyByteBuf writeVarLong(long l);
    IFriendlyByteBuf writeUUID(UUID uuid);
    IFriendlyByteBuf writeByteArray(byte[] arr);

    byte readByte();
    short readShort();
    int readInt();
    int readVarInt();
    long readLong();
    long readVarLong();
    UUID readUUID();
    byte[] readByteArray();
    byte[] readByteArray(int maxSize);

    int readerIndex();
    IFriendlyByteBuf readerIndex(int i);
    int writerIndex();
    IFriendlyByteBuf writerIndex(int i);
    IFriendlyByteBuf setIndex(int reader, int writer);

    int readableBytes();
    int writableBytes();
    int maxWritableBytes();

    boolean isReadable();
    boolean isReadable(int i);
    boolean isWritable();
    boolean isWritable(int i);
}
