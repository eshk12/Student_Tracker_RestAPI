package com.project.Utils;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class PasswordAuthentication {

    public static String hashPassword(final String password){
        return hashPassword(password.toCharArray());
    }

    public static String hashPassword(final char[] password) {
        int keyLength = 512;
        int iterations = 10000;
        String salt = "#$@123654@$#";
        byte[] saltBytes = salt.getBytes();
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password, saltBytes, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return Hex.encodeHexString(res);
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }
    public static String createLoginToken(String email, String password)
    {
        return hashPassword(email+":"+password);
    }
}
