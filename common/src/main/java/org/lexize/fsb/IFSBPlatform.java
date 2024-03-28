package org.lexize.fsb;

import java.nio.file.Path;

public interface IFSBPlatform {
    void initClientPackets(FSBClient client);
    void initServerPackets(FSBServer server);
    Path getConfigDir();
}
