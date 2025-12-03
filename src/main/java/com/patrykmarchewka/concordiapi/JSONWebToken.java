package com.patrykmarchewka.concordiapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Random;



///User logs in → Sends username & password to the server.
///Server verifies credentials → If correct, it creates a JWT.
///JWT is sent back to the user → User includes it in every request (Authorization: Bearer <JWT>).
///When a request is received, the server verifies the JWT signature to check if it was tampered with.





@Component
public class JSONWebToken {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private static String secret_key;


    /**
     * Generates new secret key
     */
    @PostConstruct
    private void swapSecret(){
        secret_key = SECRET_KEY.isBlank() ? SecureSecretKeyGenerator() : SECRET_KEY;
    }

    /**
     * Hashes data, encodes to base64 and returns it without extra padding as String
     * @param data Data to hash
     * @param secret Secret key, environment variable
     * @return HMAC hash after Base64 encoding
     * @throws NoSuchAlgorithmException Thrown when it can't use HMacSHA256 from javax.Mac class
     * @throws InvalidKeyException Thrown when key is invalid
     */
    private static String HmacSHA256(String data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(),"HmacSHA256");
        mac.init(secretKeySpec);

        byte[] hmacBytes = mac.doFinal(data.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes);
    }

    /**
     * Compares two JsonWebTokens and verifies authenticity
     * @param jwt Full three part JWT
     * @return True if recomputed JWT matches the one provided, otherwise false
     * @throws NoSuchAlgorithmException Thrown when it can't use HMacSHA256 from javax.Mac class
     * @throws InvalidKeyException Thrown when key is invalid
     */
    public static boolean VerifyJWT(String jwt) throws NoSuchAlgorithmException, InvalidKeyException {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3){
            return false;
        }

        String headerPayload = parts[0] + "." + parts[1];
        String signatureReceived = parts[2];

        String computedSignature = HmacSHA256(headerPayload,secret_key);

        return computedSignature.equals(signatureReceived);
    }

    /**
     * Creates Base64 String key without padding that is between 32 and 64 byte length <br>
     * Uses SecureRandom for safer RNG
     * @return Base64 String without padding
     */
    public static String SecureSecretKeyGenerator(){
        byte[] key = new byte[new Random().nextInt(32,65)];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().withoutPadding().encodeToString(key);
    }

    /**
     * Converts String to Base64 String without padding, uses UTF-8
     * @param input String to convert
     * @return Base64 String without padding, uses UTF-8
     */
    public static String Base64Encoding(String input){
        return Base64.getEncoder().withoutPadding().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates Json Web Token from given login and password String <br>
     * Generated token is valid for 1 hour since creation
     * @param uID ID of user requesting the token
     * @return Encoded JsonWebToken
     * @throws NoSuchAlgorithmException Thrown when it can't use HMacSHA256 from javax.Mac class
     * @throws InvalidKeyException Thrown when key is invalid
     */
    public static String GenerateJWToken(long uID) throws NoSuchAlgorithmException, InvalidKeyException {
        String header = "{\"alg\":\"HS256\",\"type\":\"JWT\"}";
        String encodedHeader = Base64Encoding(header);
        long issuedAt = System.currentTimeMillis()/1000;
        long expiry = issuedAt + 3600; //60 = 1 minute, 3600 = 1 hour,
        String payload = String.format("{\"uID\":\"%s\",\"iat\":\"%d\",\"exp\":\"%d\"}",uID,issuedAt,expiry);
        String encodedPayload = Base64Encoding(payload);
        String signature = JSONWebToken.HmacSHA256(encodedHeader + "." + encodedPayload,secret_key);
        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    /**
     * Extracts information from Json Web Token
     * @param jwt Full Json Web Token
     * @return Payload or Null if jwt is not valid
     * @throws JsonProcessingException Thrown when can't read value from payload
     */
    public static Map<String,String> ExtractJWTTokenPayload(String jwt) throws JsonProcessingException {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3){
            return null;
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(payload, new TypeReference<>() {});
    }



}
