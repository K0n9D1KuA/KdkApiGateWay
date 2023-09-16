package utils;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class HashUtils {

    //
    public static final String URL_PREFIX = "/";

    private static final char[] BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String longUrlToShortUrl(CharSequence charSequence) {
        HashCode hashCode = Hashing.murmur3_128().hashString(charSequence, StandardCharsets.UTF_8);
        long hashValue = hashCode.asLong();
        hashValue = hashValue < 0 ? -hashValue : hashValue;
        StringBuilder sb = new StringBuilder();
        while (hashValue > 0) {
            sb.append(BASE62_CHARS[(int) (hashValue % 62)]);
            hashValue /= 62;
        }
        return URL_PREFIX + sb.reverse().toString();
    }

}
