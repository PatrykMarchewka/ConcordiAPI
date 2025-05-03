package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMeDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestLogin;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@Tag(name = "Authentication and misc", description = "Authentication, user information and invitation check")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;
    @Autowired
    private InvitationService invitationService;


    @Operation(summary = "Check service status", description = "Checks if service is up and working")
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck(){
        return ResponseEntity.ok(new APIResponse<>("Service is up!",null));
    }

    @Operation(summary = "Login user", description = "Authenticate the user and return JWT Token")
    @ApiResponse(responseCode = "200",description = "Successful login, token was generated and provided")
    @ApiResponse(responseCode = "401", description = "Can't authenticate, provided credentials are wrong")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequestLogin body){

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

    @Operation(summary = "Create new user", description = "Create new user with provided credentials")
    @PostMapping("/signup")
    public ResponseEntity<?> create(@RequestBody @Validated(OnCreate.class) UserRequestBody body){
        if (userService.checkIfUserExistsByLogin(body.getLogin())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIResponse<>("Login already in use, please choose a different one",null));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("User created",new UserMemberDTO(userService.createUser(body.getLogin(), body.getPassword(), body.getName(), body.getLastName()))));
    }

    @Operation(summary = "Information about me", description = "Return information about currently logged in user")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/me")
    public ResponseEntity<?> getMyData(Authentication authentication){
        return ResponseEntity.ok(new APIResponse<>("Data related to my account", new UserMeDTO((User)authentication.getPrincipal(),teamUserRoleService)));
    }

    @Operation(summary = "Edit information about me", description = "Edit information about currently logged in user")
    @SecurityRequirement(name = "BearerAuth")
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

    @Operation(summary = "Generate new token", description = "Generates new JWT token")
    @SecurityRequirement(name = "BearerAuth")
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

    @Operation(summary = "Check invitation", description = "Returns information about provided invitation")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/invitations/{invID}")
    public ResponseEntity<?> getInfoAboutInvitation(@PathVariable String invID){
        Invitation invitation = invitationService.getInvitationByUUID(invID);
        if (invitation!= null){
            return ResponseEntity.ok(new APIResponse<>("The provided invitation information",new InvitationMemberDTO(invitation,teamUserRoleService)));
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Couldnt find invitation with provided UUID");
        }
    }

    @Operation(summary = "Join team using invitation", description = "Joins team using the provided invitation")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/invitations/{invID}")
    public ResponseEntity<?> joinTeam(@PathVariable String invID, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        Invitation invitation = invitationService.getInvitationByUUID(invID);
        if (invitation != null && !user.getTeams().contains(invitation.getTeam())){
            invitationService.useInvitation(invitation,user);
            return ResponseEntity.ok(new APIResponse<>("Joined the following team:", new TeamMemberDTO(invitation.getTeam(), user,teamUserRoleService)));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }
}
