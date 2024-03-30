package org.lexize.fsb;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.avatar.UserData;
import org.figuramc.figura.avatar.local.CacheAvatarLoader;
import org.figuramc.figura.config.ConfigType;
import org.lexize.fsb.packets.*;
import org.lexize.fsb.packets.client.*;
import org.lexize.fsb.packets.client.FSBUserDataS2C;
import org.lexize.fsb.utils.Identifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
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
    private static final ConfigType.EnumConfig SYNC_PRIORITY = new ConfigType.EnumConfig("fsb.config.sync", FSB_CATEGORY, 0, 2) {{
        name = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "sync");
        tooltip = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "sync.tooltip");
        enumList = List.of(
                Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "sync.figura"),
                Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "sync.fsb")
        );
        enumTooltip = List.of(
                Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "sync.figura.tooltip"),
                Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "sync.fsb.tooltip")
        );
    }};

    public final HashMap<Identifier, IFSBClientPacketHandler<?>> CLIENT_HANDLERS = new HashMap<>() {{
        put(FSBHandshakeS2C.ID, new FSBClientHandshakeHandler());
        put(FSBPingS2C.ID, new FSBClientPingHandler());
        put(FSBUserDataS2C.ID, new FSBUserDataHandler());
        put(FSBAvatarPartS2C.ID, new FSBAvatarPartHandler());
        put(FSBCancelAvatarLoadS2C.ID, new FSBCancelAvatarLoadHandler());
        put(FSBClearAvatarS2C.ID, new FSBClearAvatarHandler());
    }};

    private static FSBClient INSTANCE;
    private boolean connected = false;
    private boolean allowAvatars = false;
    private boolean allowPings = false;
    private final HashMap<UUID, ByteArrayOutputStream> avatarDataBuffers = new HashMap<>();
    public FSBClient() {
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
    }

    public void acceptAvatarPart(UUID avatarOwner, byte[] avatarData, boolean isFinal, String hash) throws IOException {
        ByteArrayInputStream bais;
        boolean firstRead = !avatarDataBuffers.containsKey(avatarOwner);
        if (firstRead && isFinal) {
            bais = new ByteArrayInputStream(avatarData);
        }
        else if (isFinal) {
            ByteArrayOutputStream baos = avatarDataBuffers.remove(avatarOwner);
            baos.write(avatarData);
            bais = new ByteArrayInputStream(baos.toByteArray());
        }
        else {
            ByteArrayOutputStream baos = avatarDataBuffers.computeIfAbsent(avatarOwner, (u) -> new ByteArrayOutputStream());
            baos.write(avatarData);
            return;
        }
        CompoundTag avatarCompound = NbtIo.readCompressed(bais, NbtAccounter.unlimitedHeap());
        AvatarManager.setAvatar(avatarOwner, avatarCompound);
        CacheAvatarLoader.save(hash, avatarCompound);
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
