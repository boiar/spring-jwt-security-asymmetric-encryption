package com.example.auth_security.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {
    private KeyUtils(){}

    public static PrivateKey loadPrivateKey(final String pemPath) throws Exception {
        final String key = readFromResource(pemPath).replace("-----BEGIN PRIVATE KEY-----", "")
                                                    .replace("-----END PRIVATE KEY-----", "")
                                                    .replaceAll("\\s","");

        final byte[] decode = Base64.getDecoder().decode(key);
        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decode);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }


    public static PublicKey loadPublicKey(final String pemPath) throws Exception {
        final String key = readFromResource(pemPath).replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s","");

        final byte[] decode = Base64.getDecoder().decode(key);
        final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decode);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    private static String readFromResource(String pemPath) throws Exception {

        // try with resources
        try (final InputStream inputStream = KeyUtils.class.getResourceAsStream(pemPath)){

            if (inputStream == null) {
                throw new IllegalArgumentException("Could not find key file " + pemPath);
            }
            return new String(inputStream.readAllBytes());
        }
    }
}
