package org.lexize.fsb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.lexize.fsb.packets.IFSBPacket;
import org.lexize.fsb.packets.client.FSBHandshakeS2C;
import org.lexize.fsb.packets.server.FSBServerPacketHandler;
import org.lexize.fsb.utils.Identifier;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class FSBServer {
    private static final Logger LOGGER = Logger.getLogger("FSB");
    private static FSBServer INSTANCE;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final HashMap<Identifier, FSBServerPacketHandler<?>> SERVER_PACKET_HANDLERS = new HashMap<>();
    private final HashMap<UUID, PingCounter> PING_COUNTERS = new HashMap<>();
    private FSBConfig config;
    private FSBAvatarManager avatarManager;
    private FSBDatabase database;
    private boolean allowAvatars;
    private boolean allowPings;
    private int pingResetCounter = 0;

    public FSBServer() {
        INSTANCE = this;
    }

    public void readConfig() {
        Path pathToConfig = getConfigDir().resolve("config.json");
        File configFile = pathToConfig.toFile();
        try {
            if (!configFile.exists()) {
                config = new FSBConfig();
                InputStream is = this.getClass().getResourceAsStream("/default_config.json");
                FileOutputStream fos = new FileOutputStream(configFile);
                is.transferTo(fos);
                fos.close();
            }
            else {
                FileInputStream fis = new FileInputStream(configFile);
                config = GSON.fromJson(new String(fis.readAllBytes(), StandardCharsets.UTF_8), FSBConfig.class);
                fis.close();
            }
        } catch (IOException ignored) {}
    }

    public FSBConfig getConfig() {
        return config;
    }

    public FSBDatabase getDatabase() {
        return database;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public void initialize() {
        readConfig();
        allowAvatars = config.avatarFeaturesAllowed();
        try {
            database = new FSBDatabase(getConfig());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to establish DB connection", e);
            if(config.crashIfDBErrors()) {
                throw new RuntimeException(e);
            }
            else allowAvatars = false;
        }
        avatarManager = new FSBAvatarManager(this);
        initializeServerPackets();
    }

    public void tick() {
        avatarManager.tick();
        pingResetCounter++;
        if (pingResetCounter == 20) {
            for (PingCounter counter: PING_COUNTERS.values()) counter.reset();
        }
    }

    public boolean allowPings() {
        return allowPings;
    }

    public boolean allowAvatars() {
        return allowPings;
    }

    protected abstract void initializeServerPackets();
    public void sendS2CPacket(UUID receiver, IFSBPacket packet) {
        if (receiver.version() != 4) return; // :trol:
        sendS2CPacketPlatform(receiver, packet);
    }
    protected abstract void sendS2CPacketPlatform(UUID receiver, IFSBPacket packet);
    public abstract Path getConfigDir();
    public abstract List<UUID> getPlayers();

    public void onPlayerJoin(UUID uuid) {
        sendS2CPacket(uuid, new FSBHandshakeS2C(config.avatarFeaturesAllowed(), config.pingsAllowed()));
    }

    public PingCounter getCounter(UUID player) {
        return PING_COUNTERS.computeIfAbsent(player, (u) -> new PingCounter());
    }

    public static FSBServer getInstance() {
        return INSTANCE;
    }

    public static class PingCounter {
        private PingCounter() {}
        private int size = 0;
        private int count = 0;
        public void incCount() {
            count++;
        }
        public void addSize(int size) {
            this.size += size;
        }

        public int getCount() {
            return count;
        }

        public int getSize() {
            return size;
        }

        private void reset() {
            size = 0;
            count = 0;
        }
    }
}
