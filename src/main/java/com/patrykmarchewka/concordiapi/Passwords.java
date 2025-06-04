package com.patrykmarchewka.concordiapi;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Passwords {

    /**
     * Hashes password using BCrypt, currently uses strength of 13
     * @param password String to hash
     * @return Hashed password
     */
    public static String HashPasswordBCrypt(String password){
        return new BCryptPasswordEncoder(13).encode(password);
    }

    public static String HashSHA256(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0;i< hash.length;i++){
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1){
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Compares password with hash
     * @param password String password to compare
     * @param hash Hash of the password to compare
     * @return True if password matches the hashed one, otherwise false
     */
    public static boolean CheckPasswordBCrypt(String password,String hash){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(13);
        return encoder.matches(password,hash);
    }




}
