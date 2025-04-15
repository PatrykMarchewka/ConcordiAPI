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
    public ResponseEntity<?> login(@RequestBody UserRequestBody body){

        User user = userService.getUserByLogin(body.getLogin());
        if (user != null && Passwords.CheckPasswordBCrypt(body.getPassword(),user.getPassword())){
            String token = null;
            try {
                token = JSONWebToken.GenerateJWToken(body.getLogin(),body.getPassword());
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
            if (token != null) {
                return ResponseEntity.ok(new APIResponse<>("Token",token));
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Error! Cant process request");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials!");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> create(@RequestBody UserRequestBody body){
        if (userService.checkIfUserExistsByLogin(body.getLogin())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Login already in use, please choose a different one");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("User created",new UserMemberDTO(userService.createUser(body.getLogin(), body.getPassword(), body.getName(), body.getLastName()))));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyData(Authentication authentication){
        return ResponseEntity.ok(authentication.getPrincipal());
        //return ResponseEntity.ok(new UserMemberDTO((User)authentication.getPrincipal()));
    }

    @PatchMapping("/me")
    @Transactional
    public ResponseEntity<?> changeData(@RequestBody UserRequestBody body,Authentication authentication){
        User user = (User)authentication.getPrincipal();
        if (body.getLogin() != null){
            if (userService.checkIfUserExistsByLogin(body.getLogin())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIResponse<>("Login currently in use",null));
            }
            else{
                user.setLogin(body.getLogin());
            }
        }
        if (body.getPassword() != null){
            user.setPassword(Passwords.HashPasswordBCrypt(body.getPassword()));
        }
        if (body.getName() != null){
            user.setName(body.getName());
        }
        if (body.getLastName() != null){
            user.setLastName(body.getLastName());
        }
        userService.saveUser(user);
        return ResponseEntity.ok(new APIResponse<>("Data changed!",new UserMemberDTO(user)));

    }

    @PostMapping("/me/refresh")
    public ResponseEntity<?> refreshToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        String response;
        try {
            response = JSONWebToken.GenerateJWToken(user.getLogin(),user.getPassword());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Cant generate JSON Web Token");
        }
        return ResponseEntity.ok(new APIResponse<>("Your new token",response));
    }


    @GetMapping("/invitations/{invID}")
    public ResponseEntity<?> getInfoAboutInvitation(@PathVariable String invID){
        Invitation invitation = invitationService.getInvitationByUUID(invID);
        if (invitation!= null){
            return ResponseEntity.ok(new APIResponse<>("The provided invitation information",new InvitationMemberDTO(invitation)));
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Couldnt find invitation with provided UUID");
        }
    }


    @PostMapping("/invitations/{invID}")
    public ResponseEntity<?> joinTeam(@PathVariable String invID, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        Invitation invitation = invitationService.getInvitationByUUID(invID);
        if (invitation != null && !user.getTeams().contains(invitation.getTeam())){
            invitationService.useInvitation(invitation,user);
            return ResponseEntity.ok(new APIResponse<>("Joined the following team:", new TeamMemberDTO(invitation.getTeam(), user)));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }
}
