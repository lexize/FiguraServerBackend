package org.lexize.fsb;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.avatar.UserData;
import org.figuramc.figura.avatar.local.CacheAvatarLoader;
import org.figuramc.figura.config.ConfigType;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class FSBClient {
    private static final ConfigType.Category FSB_CATEGORY = new ConfigType.Category("fsb") {{
        name = Component.translatable("fsb.config");
        tooltip = Component.translatable("fsb.config.tooltip");
    }};
    private static final String FSB_CONFIG_TRANSLATION_KEY = "fsb.config.";
    private static final List<Component> FSB_PRIORITY_TRANSLATIONS = List.of(
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.figura"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.figura_fsb"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.fsb_figura"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.fsb")
    );
    private static final List<Component> FSB_PRIORITY_TOOLTIPS_TRANSLATIONS = List.of(
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.figura.tooltip"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.figura_fsb.tooltip"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.fsb_figura.tooltip"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.fsb.tooltip")
    );
    private static final ConfigType.EnumConfig AVATARS_PRIORITY = new ConfigType.EnumConfig("fsb.config.avatars", FSB_CATEGORY, 1, 4) {{
        name = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "avatars");
        tooltip = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "avatars.tooltip");
        enumList = FSB_PRIORITY_TRANSLATIONS;
        enumTooltip = FSB_PRIORITY_TOOLTIPS_TRANSLATIONS;
    }};
    private static final ConfigType.EnumConfig PINGS_PRIORITY = new ConfigType.EnumConfig("fsb.config.pings", FSB_CATEGORY, 1, 4) {{
        name = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "pings");
        tooltip = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "pings.tooltip");
        enumList = FSB_PRIORITY_TRANSLATIONS;
        enumTooltip = FSB_PRIORITY_TOOLTIPS_TRANSLATIONS;
    }};

    public final HashMap<Identifier, FSBClientPacketHandler<?>> CLIENT_HANDLERS = new HashMap<>();

    private static FSBClient INSTANCE;
    private boolean connected = false;
    private boolean allowAvatars = false;
    private boolean allowPings = false;
    private final HashMap<UUID, Pair<UUID, ByteArrayOutputStream>> avatarDataBuffers = new HashMap<>();
    public FSBClient() {
        CLIENT_HANDLERS.put(FSBHandshakeS2C.ID, new FSBClientHandshakeHandler(this));
        CLIENT_HANDLERS.put(FSBPingS2C.ID, new FSBClientPingHandler(this));
        CLIENT_HANDLERS.put(FSBUserDataS2C.ID, new FSBUserDataHandler(this));
        CLIENT_HANDLERS.put(FSBAvatarPartS2C.ID, new FSBAvatarPartHandler(this));
        CLIENT_HANDLERS.put(FSBCancelAvatarLoadS2C.ID, new FSBCancelAvatarLoadHandler(this));
        CLIENT_HANDLERS.put(FSBClearAvatarS2C.ID, new FSBClearAvatarHandler(this));
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

    public void onConnect(boolean allowAvatars, boolean allowPings) {
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

    public void acceptAvatarPart(UUID streamId, UUID target, byte[] avatarData, boolean isFinal) throws IOException {
        ByteArrayInputStream bais;
        boolean firstRead = !avatarDataBuffers.containsKey(streamId);
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String hash;
        if (firstRead && isFinal) {
            hash = Utils.hexFromBytes(digest.digest(avatarData));
            bais = new ByteArrayInputStream(avatarData);
        }
        else if (isFinal) {
            ByteArrayOutputStream baos = avatarDataBuffers.remove(streamId).right();
            baos.write(avatarData);
            byte[] data = baos.toByteArray();
            hash = Utils.hexFromBytes(digest.digest(data));
            bais = new ByteArrayInputStream(data);
        }
        else {
            Pair<UUID, ByteArrayOutputStream> pair = avatarDataBuffers.computeIfAbsent(streamId, (u) -> new Pair<>(target, new ByteArrayOutputStream()));
            pair.right().write(avatarData);
            return;
        }
        CompoundTag avatarCompound = NbtIo.readCompressed(bais, NbtAccounter.unlimitedHeap());
        CacheAvatarLoader.save(hash, avatarCompound);
        getUserData().computeIfAbsent(target, UserData::new).loadAvatar(avatarCompound);
    }

    public void cancelAvatarLoad(UUID avatarOwner) {
        avatarDataBuffers.remove(avatarOwner);
    }

    public static FSBClient instance() {
        return INSTANCE;
    }
    public static FSBPriority getPingsPriority() {
        return FSBPriority.fromId(PINGS_PRIORITY.value);
    }
    public static FSBPriority getAvatarsPriority() {
        return FSBPriority.fromId(AVATARS_PRIORITY.value);
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
