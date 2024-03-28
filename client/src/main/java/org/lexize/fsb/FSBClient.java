package org.lexize.fsb;

import java.util.HashMap;
import java.util.List;

import net.minecraft.network.chat.Component;
import org.figuramc.figura.config.ConfigType;
import org.lexize.fsb.packets.FSBClientPingHandler;
import org.lexize.fsb.packets.IFSBClientPacketHandler;
import org.lexize.fsb.packets.client.FSBPingS2C;
import org.lexize.fsb.utils.Identifier;

public class FSBClient {
    private static final ConfigType.Category FSB_CATEGORY = new ConfigType.Category("fsb");
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
    public static final ConfigType.EnumConfig AVATARS_PRIORITY = new ConfigType.EnumConfig("fsb.avatars", FSB_CATEGORY, 1, 5) {{
        name = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.avatars");
        enumList = FSB_PRIORITY_TRANSLATIONS;
        enumTooltip = FSB_PRIORITY_TOOLTIPS_TRANSLATIONS;
    }};
    public static final ConfigType.EnumConfig PINGS_PRIORITY = new ConfigType.EnumConfig("fsb.avatars", FSB_CATEGORY, 1, 5) {{
        name = Component.translatable(FSB_CONFIG_TRANSLATION_KEY + "priority.avatars");
        enumList = FSB_PRIORITY_TRANSLATIONS;
        enumTooltip = FSB_PRIORITY_TOOLTIPS_TRANSLATIONS;
    }};

    private static boolean connected;

    public static boolean isConnected() {
        return connected;
    }

    public static void setConnected(boolean state) {
        connected = state;
    }

    public static final HashMap<Identifier, IFSBClientPacketHandler<?>> CLIENT_HANDLERS = new HashMap<>() {{
        CLIENT_HANDLERS.put(FSBPingS2C.ID, new FSBClientPingHandler());
    }};
}
