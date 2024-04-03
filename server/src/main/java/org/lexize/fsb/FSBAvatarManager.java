package org.lexize.fsb;

import org.lexize.fsb.packets.client.FSBCancelAvatarLoadS2C;
import org.lexize.fsb.utils.Pair;
import org.lexize.fsb.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class FSBAvatarManager {
    private final int gcTicks;
    private final FSBServer parent;
    private final HashMap<String, AvatarHolder> loadedAvatars = new HashMap<>();
    private final HashMap<String, ArrayList<AvatarStreaming>> avatarStreaming = new HashMap<>();
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

    public String addAvatar(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            String hashString = Utils.hexFromBytes(hash);
            loadedAvatars.put(hashString, new AvatarHolder(data));
            return hashString;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<UUID> release(String prevHash) {
        ArrayList<UUID> receivers = new ArrayList<>();
        var streams = avatarStreaming.get(prevHash);
        if (streams != null) {
            for (var stream : streams) {
                receivers.add(stream.getReceiver());
                parent.sendS2CPacket(stream.getReceiver(), new FSBCancelAvatarLoadS2C(stream.getId()));
            }
        }
        loadedAvatars.remove(prevHash);
        return receivers;
    }

    public byte[] getOrLoad(String hash) {
        var holder = loadedAvatars.computeIfAbsent(hash, this::load);
        if (holder == null) return null;
        holder.resetTicks();
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

    private static class AvatarStreaming {
        private int position;
        private UUID receiver;
        private UUID id;

        public int getPosition() {
            return position;
        }

        public UUID getReceiver() {
            return receiver;
        }

        public UUID getId() {
            return id;
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
