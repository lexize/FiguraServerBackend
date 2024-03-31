package org.lexize.fsb.utils;

import java.util.UUID;

public class Utils {
    private static final char[] HEX_CHARS = new char[] {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };
    public static String hexFromBytes(byte[] arr, boolean reverse) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            byte b = arr[i];
            char c1 = HEX_CHARS[(b >> 4) & 0xF];
            char c2 = HEX_CHARS[b & 0xF];
            if (reverse) {
                sb.insert(0, c2);
                sb.insert(0, c1);
            }
            else {
                sb.append(c1);
                sb.append(c2);
            }
        }
        return sb.toString();
    }

    public static String hexFromBytes(byte[] arr) {
        return hexFromBytes(arr, false);
    }

    public static String uuidToHex(UUID uuid) {
        return hexFromBytes(toBytes(uuid.getMostSignificantBits())) + hexFromBytes(toBytes(uuid.getLeastSignificantBits()));
    }

    public static byte[] toBytes(int i) {
        return new byte[] {
                (byte) ((i >> 24) & 0xFF),
                (byte) ((i >> 16) & 0xFF),
                (byte) ((i >> 8) & 0xFF),
                (byte) (i & 0xFF),
        };
    }

    public static byte[] toBytes(long l) {
        return new byte[] {
                (byte) ((l >> 56) & 0xFF),
                (byte) ((l >> 48) & 0xFF),
                (byte) ((l >> 40) & 0xFF),
                (byte) ((l >> 32) & 0xFF),
                (byte) ((l >> 24) & 0xFF),
                (byte) ((l >> 16) & 0xFF),
                (byte) ((l >> 8) & 0xFF),
                (byte) (l & 0xFF)
        };
    }
}
