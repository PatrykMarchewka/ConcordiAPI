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
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body){
        String login = body.get("login");
        String password = body.get("password");

        User user = userService.getUserByLogin(login);
        if (user != null && Passwords.CheckPasswordBCrypt(password,user.getPassword())){
            String token = null;
            try {
                token = JSONWebToken.GenerateJWToken(login,password);
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

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Map<String,String> body){
        if (userService.checkIfUserExistsByLogin(body.get("login"))){
            ResponseEntity.status(HttpStatus.CONFLICT).body("Login already in use, please choose a different one");
        }
        userService.createUser(body.get("login"),body.get("password"),body.get("name"),body.get("lastName"));
        return ResponseEntity.status(HttpStatus.CREATED).body("User created!");
    }
}
