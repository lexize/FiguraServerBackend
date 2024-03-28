package org.lexize.fsb;

import java.nio.file.Path;

public class FSBPlatform implements IFSBPlatform {

    @Override
    public void initClientPackets(FSBClient client) {
        throw new AssertionError();
    }

    @Override
    public void initServerPackets(FSBServer server) {
        throw new AssertionError();
    }

    @Override
    public Path getConfigDir() {
        throw new AssertionError();
    }
}
