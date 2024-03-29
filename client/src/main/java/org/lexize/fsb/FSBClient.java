package org.lexize.fsb;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.config.ConfigType;
import org.lexize.fsb.packets.FSBClientHandshakeHandler;
import org.lexize.fsb.packets.FSBClientPingHandler;
import org.lexize.fsb.packets.IFSBClientPacketHandler;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.packets.client.FSBHandshakeS2C;
import org.lexize.fsb.packets.client.FSBPingS2C;
import org.lexize.fsb.utils.Identifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class FSBClient {
    private static final ConfigType.Category FSB_CATEGORY = new ConfigType.Category("fsb") {{
        name = Component.translatable("fsb.config");
        tooltip = Component.translatable("fsb.config.tooltip");
    }};
    private static final String FSB_CONFIG_TRANSLATION_KEY = "fsb.config.";
    private static final List<Component> FSB_PRIORITY_TRANSLATIONS = List.of(
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.1"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.2"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.3"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.4"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.5")
    );
    private static final List<Component> FSB_PRIORITY_TOOLTIPS_TRANSLATIONS = List.of(
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.1.tooltip"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.2.tooltip"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.3.tooltip"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.4.tooltip"),
            Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.5.tooltip")
    );
    private static final ConfigType.EnumConfig AVATARS_PRIORITY = new ConfigType.EnumConfig("fsb.config.avatars", FSB_CATEGORY, 1, 5) {{
        name = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "avatars");
        tooltip = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "avatars.tooltip");
        enumList = FSB_PRIORITY_TRANSLATIONS;
        enumTooltip = FSB_PRIORITY_TOOLTIPS_TRANSLATIONS;
    }};
    private static final ConfigType.EnumConfig PINGS_PRIORITY = new ConfigType.EnumConfig("fsb.config.pings", FSB_CATEGORY, 1, 5) {{
        name = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "pings");
        tooltip = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "pings.tooltip");
        enumList = FSB_PRIORITY_TRANSLATIONS;
        enumTooltip = FSB_PRIORITY_TOOLTIPS_TRANSLATIONS;
    }};
    private static final ConfigType.EnumConfig SYNC_PRIORITY = new ConfigType.EnumConfig("fsb.config.sync", FSB_CATEGORY, 0, 2) {{
        name = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "sync");
        tooltip = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "sync.tooltip");
        enumList = List.of(
                Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "sync.1"),
                Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "sync.2")
        );
        enumTooltip = List.of(
                Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "sync.1.tooltip"),
                Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "sync.2.tooltip")
        );
    }};

    public final HashMap<Identifier, IFSBClientPacketHandler<?>> CLIENT_HANDLERS = new HashMap<>() {{
        put(FSBPingS2C.ID, new FSBClientPingHandler());
        put(FSBHandshakeS2C.ID, new FSBClientHandshakeHandler());
    }};

    private static FSBClient INSTANCE;
    private boolean connected = false;
    private HashMap<UUID, ByteArrayOutputStream> avatarDataBuffers = new HashMap<>();
    public FSBClient() {
        INSTANCE = this;
    }

    public abstract void sendC2SPacket(IFSBPacket packet);
    public abstract void initializeClientPackets();

    public boolean isConnected() {
        return connected;
    }

    public void onConnect() {
        connected = true;
    }

    public void onDisconnect() {
        connected = false;
    }

    public void acceptAvatarPart(UUID avatarOwner, byte[] avatarData, boolean isFinal) throws IOException {
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
        FSBPriority priority = getPingsPriority();
        if (priority != FSBPriority.FIGURA_AND_FSB) return priority;
        return SYNC_PRIORITY.value == 0 ? FSBPriority.FIGURA_THEN_FSB : FSBPriority.FSB_THEN_FIGURA;
    }
    public enum FSBPriority {
        FIGURA_ONLY,
        FIGURA_THEN_FSB,
        FIGURA_AND_FSB,
        FSB_THEN_FIGURA,
        FSB_ONLY;
        public static FSBPriority fromId(int i) {
            return switch (i) {
                case 0 -> FIGURA_ONLY;
                case 1 -> FIGURA_THEN_FSB;
                case 2 -> FIGURA_AND_FSB;
                case 3 -> FSB_THEN_FIGURA;
                case 4 -> FSB_ONLY;
                default -> null;
            };
        }
    }
}
