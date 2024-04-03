package org.lexize.fsb;

import org.lexize.fsb.utils.Utils;

import java.util.UUID;

public class Test {
    public static void main(String[] args) {
        String hex = Utils.uuidToHex(UUID.randomUUID());
        System.out.println(hex);
        byte[] h = Utils.bytesFromHex(hex, true);
        for (byte b : h) {
            System.out.print(Integer.toHexString(b & 0Xff));
        }
        System.out.println();
        String hex2 = Utils.hexFromBytes(h);
        System.out.println(hex2);
    }
}
