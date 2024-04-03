package org.lexize.fsb;

public interface FSBConfig {
    FSBClient.FSBPriority getAvatarsPriority();
    FSBClient.FSBPriority getPingsPriority();
    String getEPassword();
    int maxAvatarPartSize();
}
