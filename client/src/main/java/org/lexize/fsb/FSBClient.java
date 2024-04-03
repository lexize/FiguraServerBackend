package org.lexize.fsb;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.avatar.UserData;
import org.figuramc.figura.avatar.local.CacheAvatarLoader;
import org.figuramc.figura.gui.FiguraToast;
import org.lexize.fsb.packets.*;
import org.lexize.fsb.packets.client.*;
import org.lexize.fsb.packets.client.FSBUserDataS2C;
import org.lexize.fsb.utils.Identifier;
import org.lexize.fsb.utils.Pair;
import org.lexize.fsb.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class FSBClient {

    public final HashMap<Identifier, FSBClientPacketHandler<?>> CLIENT_HANDLERS = new HashMap<>();

    private static FSBClient INSTANCE;
    private boolean connected = false;
    private boolean allowAvatars = false;
    private boolean allowPings = false;
    private final HashMap<UUID, Pair<ByteArrayOutputStream, String>> avatarDataBuffers = new HashMap<>();
    private final HashMap<String, ArrayList<UUID>> avatarFetchTargets = new HashMap<>();
    private final ArrayList<String> expectedHashes = new ArrayList<>();
    private FSBConfig configInstance;
    public FSBClient() {
        CLIENT_HANDLERS.put(FSBHandshakeS2C.ID, new FSBClientHandshakeHandler(this));
        CLIENT_HANDLERS.put(FSBPingS2C.ID, new FSBClientPingHandler(this));
        CLIENT_HANDLERS.put(FSBUserDataS2C.ID, new FSBUserDataHandler(this));
        CLIENT_HANDLERS.put(FSBAvatarPartS2C.ID, new FSBAvatarPartHandler(this));
        CLIENT_HANDLERS.put(FSBCancelAvatarLoadS2C.ID, new FSBCancelAvatarLoadHandler(this));
        CLIENT_HANDLERS.put(FSBClearAvatarS2C.ID, new FSBClearAvatarHandler(this));
        CLIENT_HANDLERS.put(FSBNotifyS2C.ID, new FSBNotifyHandler(this));
        INSTANCE = this;
    }

    public abstract void sendC2SPacket(IFSBPacket packet);
    protected abstract void initializeClientPackets();

    public boolean isConnected() {
        return connected;
    }

    public boolean allowPings() {
        return allowPings;
    }

    public boolean allowAvatars() {
        return allowAvatars;
    }

    public void expectHash(String hash) {
        expectedHashes.add(hash);
    }

    public boolean expectingHash(String hash) {
        return expectedHashes.contains(hash);
    }

    public void onConnectServer(SocketAddress remoteAddress) {

    }
    public void onConnectFSB(boolean allowAvatars, boolean allowPings) {
        connected = true;
        this.allowAvatars = allowAvatars;
        this.allowPings = allowPings;
    }

    public void onDisconnect() {
        connected = false;
        this.allowPings = false;
        this.allowAvatars = false;
        avatarDataBuffers.clear();
    }

    public void initConfig(FSBConfig configInstance) {
        this.configInstance = configInstance;
    }

    public void acceptAvatarPart(UUID streamId, byte[] avatarData, boolean isFinal) throws IOException {
        if (expectedHashes.isEmpty()) return;
        ByteArrayOutputStream baos = avatarDataBuffers.get(streamId).left();
        baos.write(avatarData);
        if (isFinal) {
            Pair<ByteArrayOutputStream, String> baosAndHash = avatarDataBuffers.remove(streamId);
            byte[] finalAvatarData = baos.toByteArray();
            String resultHash = Utils.hexFromBytes(Utils.getHash(finalAvatarData));
            if (!resultHash.equals(baosAndHash.right())) {
                FiguraToast.sendToast(
                        Component.translatable("fsb.security.wrong_hash"),
                        Component.translatable("fsb.security.wrong_hash.desc"),
                        FiguraToast.ToastType.ERROR
                );
                return;
            }
            CompoundTag avatarTag = NbtIo.readCompressed(new ByteArrayInputStream(finalAvatarData), NbtAccounter.unlimitedHeap());
            CacheAvatarLoader.save(resultHash, avatarTag);
            ArrayList<UUID> targets = avatarFetchTargets.remove(resultHash);
            if (targets != null) {
                for (UUID target: targets) {
                    getUserData().computeIfAbsent(target, UserData::new).loadAvatar(avatarTag);
                }
            }
        }
    }

    public void prepareStream(UUID streamID, String expectedHash) {
            avatarDataBuffers.put(streamID, new Pair<>(new ByteArrayOutputStream(), expectedHash));
    }

    public boolean checkEHash(String avatarHash, String eHash) {
        String userEHash = getEHash(Utils.bytesFromHex(avatarHash));
        return userEHash.equals(eHash);
    }

    public boolean isUUIDLocal(UUID uuid) {
        var pl = Minecraft.getInstance().player;
        return pl != null && pl.getUUID().equals(uuid);
    }

    public String getEHash(byte[] hash) {
        byte[] passhash = Utils.getHash(getEPass().getBytes(StandardCharsets.UTF_8));
        byte[] finalSum = new byte[hash.length + passhash.length];
        System.arraycopy(hash, 0, finalSum, 0, hash.length);
        System.arraycopy(passhash, 0, finalSum, hash.length, passhash.length);
        return Utils.hexFromBytes(Utils.getHash(finalSum));
    }

    public void addToFetchConsumers(String hash, UUID target) {
        ArrayList<UUID> s = avatarFetchTargets.computeIfAbsent(hash, h -> new ArrayList<>());
        if (!s.contains(target)) s.add(target);
    }

    public void cancelAvatarLoad(UUID avatarOwner) {
        avatarDataBuffers.remove(avatarOwner);
    }

    public static FSBClient instance() {
        return INSTANCE;
    }
    public static FSBPriority getPingsPriority() {
        return FSBPriority.fromId(INSTANCE.configInstance != null ? INSTANCE.configInstance.getPingsPriority() : 0);
    }
    public static FSBPriority getAvatarsPriority() {
        return FSBPriority.fromId(INSTANCE.configInstance != null ? INSTANCE.configInstance.getAvatarsPriority() : 0);
    }

    private static String getEPass() {
        return INSTANCE.configInstance != null ? INSTANCE.configInstance.getEPassword() : null;
    }

    public static FSBPriority getSyncPriority() {
        return getPingsPriority();
    }
    public static Map<UUID, UserData> getUserData() {
        try {
            Class<AvatarManager> managerClass = AvatarManager.class;
            Field f = managerClass.getDeclaredField("LOADED_USERS");
            return (Map<UUID, UserData>) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public enum FSBPriority {
        FIGURA_ONLY,
        FIGURA_THEN_FSB,
        FSB_THEN_FIGURA,
        FSB_ONLY;
        public static FSBPriority fromId(int i) {
            return switch (i) {
                case 0 -> FIGURA_ONLY;
                case 1 -> FIGURA_THEN_FSB;
                case 2 -> FSB_THEN_FIGURA;
                case 3 -> FSB_ONLY;
                default -> null;
            };
        }
    }
}
