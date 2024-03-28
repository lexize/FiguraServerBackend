package org.lexize.fsb.utils;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class FriendlyBufWrapper implements IFriendlyByteBuf {
    private final FriendlyByteBuf source;
    public FriendlyBufWrapper(FriendlyByteBuf source) {
        this.source = source;
    }

    @Override
    public byte[] readByteArray() {
        return source.readByteArray();
    }

    @Override
    public FriendlyBufWrapper writeByteArray(byte[] array) {
        source.writeByteArray(array);
        return this;
    }

    @Override
    public byte[] readByteArray(int maxSize) {
        return source.readByteArray(maxSize);
    }

    @Override
    public int readVarInt() {
        return source.readVarInt();
    }

    @Override
    public long readVarLong() {
        return source.readVarLong();
    }

    @Override
    public FriendlyBufWrapper writeUUID(UUID uuid) {
        source.writeUUID(uuid);
        return this;
    }

    @Override
    public UUID readUUID() {
        return source.readUUID();
    }

    @Override
    public FriendlyBufWrapper writeVarInt(int value) {
        source.writeVarInt(value);
        return this;
    }

    @Override
    public FriendlyBufWrapper writeVarLong(long value) {
        source.writeVarLong(value);
        return this;
    }

    @Override
    public int readerIndex() {
        return source.readerIndex();
    }

    @Override
    public FriendlyBufWrapper readerIndex(int i) {
        source.readerIndex(i);
        return this;
    }

    @Override
    public int writerIndex() {
        return source.writerIndex();
    }

    @Override
    public FriendlyBufWrapper writerIndex(int i) {
        source.writerIndex(i);
        return this;
    }

    @Override
    public FriendlyBufWrapper setIndex(int i, int j) {
        source.setIndex(i, j);
        return this;
    }

    @Override
    public int readableBytes() {
        return source.readableBytes();
    }

    @Override
    public int writableBytes() {
        return source.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return source.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return source.isReadable();
    }

    @Override
    public boolean isReadable(int i) {
        return source.isReadable(i);
    }

    @Override
    public boolean isWritable() {
        return source.isWritable();
    }

    @Override
    public boolean isWritable(int i) {
        return source.isWritable(i);
    }

    @Override
    public byte readByte() {
        return source.readByte();
    }

    @Override
    public short readShort() {
        return source.readShort();
    }

    @Override
    public int readInt() {
        return source.readInt();
    }

    @Override
    public long readLong() {
        return source.readLong();
    }

    @Override
    public FriendlyBufWrapper writeInt(int i) {
        source.writeInt(i);
        return this;
    }

    @Override
    public FriendlyBufWrapper writeLong(long l) {
        source.writeLong(l);
        return this;
    }

    @Override
    public IFriendlyByteBuf writeByte(int b) {
        source.writeByte(b);
        return this;
    }

    @Override
    public IFriendlyByteBuf writeShort(int s) {
        source.writeShort(s);
        return this;
    }
}
