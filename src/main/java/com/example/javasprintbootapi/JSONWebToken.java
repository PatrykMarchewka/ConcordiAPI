package com.example.javasprintbootapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

    @PostConstruct
    private void swapSecret(){
        if (SECRET_KEY.isBlank()){
            secret_key = SecureSecretKeyGenerator();
        }
        else{
            secret_key = SECRET_KEY;
        }

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

        String computedSignature = HmacSHA256(headerPayload,secret_key);

        return computedSignature.equals(signatureReceived);
    }

    public static String SecureSecretKeyGenerator(){
        byte[] key = new byte[new Random().nextInt(32,65)];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().withoutPadding().encodeToString(key);
    }

    public static String Base64Encoding(String input){
        return Base64.getEncoder().withoutPadding().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String GenerateJWToken(String login, String password) throws NoSuchAlgorithmException, InvalidKeyException {
        String header = "{\"alg\":\"HS256\",\"type\":\"JWT\"}";
        String encodedHeader = Base64Encoding(header);
        long issuedAt = System.currentTimeMillis()/1000;
        long expiry = issuedAt + 3600; //60 = 1 minute, 3600 = 1 hour,
        String payload = String.format("{\"login\":\"%s\",\"password\":\"%s\",\"iat\":\"%d\",\"exp\":\"%d\"}",login,password,issuedAt,expiry);
        String encodedPayload = Base64Encoding(payload);
        String signature = JSONWebToken.HmacSHA256(encodedHeader + "." + encodedPayload,secret_key);
        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    public static Map<String,Object> ExtractJWTTokenPayload(String jwt) throws JsonProcessingException {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3){
            return null;
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(payload,Map.class);
    }



}
