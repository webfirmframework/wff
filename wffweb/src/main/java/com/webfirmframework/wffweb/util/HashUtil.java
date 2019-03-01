package com.webfirmframework.wffweb.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 *
 * @author WFF
 * @since 3.0.1
 */
public final class HashUtil {

    public static final String SHA_512 = "SHA-512";

    public static final String SHA_384 = "SHA-384";

    public static final String SHA_256 = "SHA-256";

    private HashUtil() {
        throw new AssertionError();
    }

    /**
     * @param s
     * @return the SHA-256 hash of the given string in base64 encoding.
     * @throws NoSuchAlgorithmException
     */
    public static String hashSHA256(final String s)
            throws NoSuchAlgorithmException {
        return hashInBase64(s, SHA_256);
    }

    /**
     * @param s
     * @return the SHA-384 hash of the given string in base64 encoding.
     * @throws NoSuchAlgorithmException
     */
    public static String hashSHA384(final String s)
            throws NoSuchAlgorithmException {
        return hashInBase64(s, SHA_384);
    }

    /**
     * @param s
     * @return the SHA-512 hash of the given string in base64 encoding.
     * @throws NoSuchAlgorithmException
     */
    public static String hashSHA512(final String s)
            throws NoSuchAlgorithmException {
        return hashInBase64(s, SHA_512);
    }

    /**
     * @param bytes
     * @return the base64 string from the given utf-8 bytes. If the returned
     *         base64 string is converted to bytes it will be utf-8 encoded.
     * @since 3.0.1
     */
    public static String base64FromUtf8Bytes(final byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes),
                StandardCharsets.UTF_8);
    }

    /**
     * @param s
     * @param algo
     *                 eg: SHA-256, SHA-384 or SHA-512
     * @return the hash of the given string with the given algo.
     * @throws NoSuchAlgorithmException
     */
    public static String hashInBase64(final String s, final String algo)
            throws NoSuchAlgorithmException {
        return base64FromUtf8Bytes(MessageDigest.getInstance(algo)
                .digest(s.getBytes(StandardCharsets.UTF_8)));
    }
}
