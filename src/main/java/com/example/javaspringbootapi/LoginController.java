package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.*;
import com.example.javaspringbootapi.DatabaseModel.Invitation;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;
import com.example.javaspringbootapi.DatabaseModel.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;
    @Autowired
    private InvitationService invitationService;

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

    @GetMapping("/me")
    public ResponseEntity<?> getMyData(Authentication authentication){
        return ResponseEntity.ok(authentication.getPrincipal());
        //return ResponseEntity.ok(new UserMemberDTO((User)authentication.getPrincipal()));
    }

    @PatchMapping("/me")
    @Transactional
    public ResponseEntity<?> changeData(@RequestBody Map<String,String> body,Authentication authentication){
        User user = (User)authentication.getPrincipal();
        String response = "Data changed! ";
        if (body.containsKey("login")){
            if (userService.checkIfUserExistsByLogin(body.get("login"))){
                response += "New login is taken, using old login";
            }
            else{
                user.setLogin(body.get("login"));
            }
        }
        if (body.containsKey("password")){
            user.setPassword(body.get("password"));
        }
        if (body.containsKey("name")){
            user.setName(body.get("name"));
        }
        if (body.containsKey("lastname")){
            user.setLastName(body.get("lastname"));
        }
        userService.saveUser(user);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/me/refresh")
    public ResponseEntity<?> refreshToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        String response;
        try {
            response = JSONWebToken.GenerateJWToken(user.getLogin(),user.getPassword());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Cant generate JSON Token");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/join/{invID}")
    public ResponseEntity<?> joinTeam(@PathVariable String invID, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        Invitation invitation = invitationService.getInvitationByUUID(invID);
        if (invitation != null && !user.getTeams().contains(invitation.getTeam())){
            invitationService.useInvitation(invitation,user);
            if (invitation.getRole().equals(PublicVariables.UserRole.ADMIN) || invitation.getRole().equals(PublicVariables.UserRole.MANAGER)){
                return ResponseEntity.ok(new InvitationManagerDTO(invitation));
            }
            else{
                return ResponseEntity.ok(new InvitationMemberDTO(invitation));
            }

        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }
}
