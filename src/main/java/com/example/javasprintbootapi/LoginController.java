package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.User;
import com.example.javasprintbootapi.DatabaseModel.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

@RestController
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body){
        String login = body.get("login");
        String password = body.get("password");
        String role = body.get("role");

        User user = userRepository.findByLogin(login);
        if (user != null && Passwords.CheckPasswordBCrypt(password,user.getPassword()) && user.getRole().name().equalsIgnoreCase(role)){
            String token = null;
            try {
                token = JSONWebToken.GenerateJWToken(login,password,role);
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
            if (token != null) {
                return ResponseEntity.ok(Map.of("token",token));
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Error! Cant process request");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials!");

    }
}
