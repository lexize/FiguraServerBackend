package org.lexize.fsb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class FSBAvatarManager {
    private final int gcTicks;
    private final FSBServer parent;
    private final HashMap<String, AvatarHolder> loadedAvatars = new HashMap<>();

    public FSBAvatarManager(FSBServer parent) {
        gcTicks = parent.getConfig().getAvatarGCTicks();
        this.parent = parent;
    }

    public void tick() {
        ArrayList<String> forDeletion = new ArrayList<>();
        for (var entry : loadedAvatars.entrySet()) {
            entry.getValue().tick();
            if (entry.getValue().ticksPassed >= gcTicks) forDeletion.add(entry.getKey());
        }
        for (String k: forDeletion) {
            loadedAvatars.remove(k);
        }
    }

    public byte[] getOrLoad(String hash) {
        var holder = loadedAvatars.computeIfAbsent(hash, this::load);
        if (holder == null) return null;
        return holder.avatarData;
    }

    private AvatarHolder load(String hash) {
        try {
            return new AvatarHolder(parent.getDatabase().getAvatarData(hash));
        }
        catch (Exception ignored) {
            return null;
        }
    }

    private static class AvatarHolder {
        private int ticksPassed;
        private byte[] avatarData;

        private AvatarHolder(byte[] data) {
            avatarData = data;
            ticksPassed = 0;
        }

        void tick() {
            ticksPassed++;
        }

        public int getTicksPassed() {
            return ticksPassed;
        }

        public byte[] getAvatarData() {
            return avatarData;
        }

        public void resetTicks() {
            ticksPassed = 0;
        }
    }
}
