package com.example.javasprintbootapi;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;



///User logs in → Sends username & password to the server.
///Server verifies credentials → If correct, it creates a JWT.
///JWT is sent back to the user → User includes it in every request (Authorization: Bearer <JWT>).
///When a request is received, the server verifies the JWT signature to check if it was tampered with.






public class JSONWebToken {
    private static String SECRET_KEY;
    private static String JWT;

    public static void setJWT(String jwt){
        JWT = jwt;
    }

    public static String getJWT(){
        return JWT;
    }

    public static void setSecretKey(String secretKey){
        SECRET_KEY = secretKey;
    }

    public static String getSecretKey(){
        return SECRET_KEY;
    }

    private static String HmacSHA256(String data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(),"HmacSHA256");
        mac.init(secretKeySpec);

        byte[] hmacBytes = mac.doFinal(data.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes);
    }

    public static boolean VerifyJWT(String jwt) throws NoSuchAlgorithmException, InvalidKeyException {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3){
            return false;
        }

        String headerPayload = parts[0] + "." + parts[1];
        String signatureReceived = parts[2];

        String computedSignature = HmacSHA256(headerPayload,SECRET_KEY);

        return computedSignature.equals(signatureReceived);
    }

    public static String SecureKeyGenerator(){
        byte[] key = new byte[new Random().nextInt(32,65)];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().withoutPadding().encodeToString(key);
    }

    public static String Base64Encoding(String input){
        return Base64.getEncoder().withoutPadding().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String GenerateJWToken(String login, String password, String role) throws NoSuchAlgorithmException, InvalidKeyException {
        String header = "{\"alg\":\"HS256\",\"type\":\"JWT\"}";
        String encodedHeader = Base64Encoding(header);
        String payload = String.format("{\"login\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",login,password,role);
        String encodedPayload = Base64Encoding(payload);
        String signature = JSONWebToken.HmacSHA256(encodedHeader + "." + encodedPayload,SECRET_KEY);
        return encodedHeader + "." + encodedPayload + "." + signature;
    }


}
