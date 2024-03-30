package org.lexize.fsb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.lexize.fsb.packets.IFSBPacket;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

public abstract class FSBServer {
    private static FSBServer INSTANCE;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private FSBConfig config;

    public FSBServer() {
        INSTANCE = this;
    }

    public void readConfig() {
        Path pathToConfig = getConfigDir().resolve("config.json");
        File configFile = pathToConfig.toFile();
        try {
            if (!configFile.exists()) {
                config = new FSBConfig();
                InputStream is = this.getClass().getResourceAsStream("default_config.json");
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

    protected abstract void initializeServerPackets();
    public abstract void sendS2CPacket(UUID receiver, IFSBPacket packet);
    public abstract Path getConfigDir();

    public static FSBServer getInstance() {
        return INSTANCE;
    }
}
