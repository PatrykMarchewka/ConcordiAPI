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
import com.patrykmarchewka.concordiapi.Exceptions.*;
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
    @ApiResponse(responseCode = "500", description = "Problem generating token, check error message for details")
    @PostMapping("/login")
    public ResponseEntity<APIResponse<String>> login(@RequestBody @Valid UserRequestLogin body){

        User user = userService.getUserByLogin(body.getLogin());
        if (user != null && Passwords.CheckPasswordBCrypt(body.getPassword(),user.getPassword())){
            String token = null;
            try {
                token = JSONWebToken.GenerateJWToken(body.getLogin(),body.getPassword());
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new JWTException(e.getMessage(),e);
            }
            if (token != null) {
                return ResponseEntity.ok(new APIResponse<>("Token",token));
            }
            else{
                throw new RuntimeException("Token is set to null");
            }
        }
        throw new WrongCredentialsException();
    }

    @Operation(summary = "Create new user", description = "Create new user with provided credentials")
    @ApiResponse(responseCode = "201", description = "User has been created")
    @ApiResponse(responseCode = "409", description = "Login already in use")
    @PostMapping("/signup")
    public ResponseEntity<APIResponse<UserMemberDTO>> create(@RequestBody @Validated(OnCreate.class) UserRequestBody body){
        if (userService.checkIfUserExistsByLogin(body.getLogin())){
            throw new ConflictException("Login already in use, please choose a different one");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("User created",new UserMemberDTO(userService.createUser(body.getLogin(), body.getPassword(), body.getName(), body.getLastName()))));
    }

    @Operation(summary = "Information about me", description = "Return information about currently logged in user")
    @ApiResponse(responseCode = "200", description = "Data related to account was provided")
    @ApiResponse(responseCode = "401", description = "You are not authenticated")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/me")
    public ResponseEntity<APIResponse<UserMeDTO>> getMyData(Authentication authentication){
        return ResponseEntity.ok(new APIResponse<>("Data related to my account", new UserMeDTO((User)authentication.getPrincipal(),teamUserRoleService)));
    }

    @Operation(summary = "Edit information about me", description = "Edit information about currently logged in user")
    @ApiResponse(responseCode = "200",description = "Data has been changed")
    @ApiResponse(responseCode = "401", description = "You are not authenticated")
    @ApiResponse(responseCode = "409", description = "Login already in use")
    @SecurityRequirement(name = "BearerAuth")
    @PatchMapping("/me")
    @Transactional
    public ResponseEntity<APIResponse<UserMemberDTO>> changeData(@RequestBody UserRequestBody body,Authentication authentication){
        User user = (User)authentication.getPrincipal();
        if (body.getLogin() != null){
            if (userService.checkIfUserExistsByLogin(body.getLogin())){
                throw new ConflictException("Login currently in use");
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
    @ApiResponse(responseCode = "200",description = "New token was generated and provided")
    @ApiResponse(responseCode = "401", description = "You are not authenticated")
    @ApiResponse(responseCode = "500", description = "Problem generating token, check error message for details")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/me/refresh")
    public ResponseEntity<APIResponse<String>> refreshToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        String response;
        try {
            response = JSONWebToken.GenerateJWToken(user.getLogin(),user.getPassword());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new JWTException(e.getMessage(),e);
        }
        return ResponseEntity.ok(new APIResponse<>("Your new token",response));
    }

    @Operation(summary = "Check invitation", description = "Returns information about provided invitation")
    @ApiResponse(responseCode = "200", description = "Data about invitation was provided")
    @ApiResponse(responseCode = "401", description = "You are not authenticated")
    @ApiResponse(responseCode = "404", description = "Cant find invitation with provided UUID")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/invitations/{invID}")
    public ResponseEntity<APIResponse<InvitationMemberDTO>> getInfoAboutInvitation(@PathVariable String invID){
        Invitation invitation = invitationService.getInvitationByUUID(invID);
        if (invitation!= null){
            return ResponseEntity.ok(new APIResponse<>("The provided invitation information",new InvitationMemberDTO(invitation,teamUserRoleService)));
        }
        else{
            throw new NotFoundException();
        }
    }

    @Operation(summary = "Join team using invitation", description = "Joins team using the provided invitation")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponse(responseCode = "200", description = "Joined the team")
    @ApiResponse(responseCode = "401", description = "You are not authenticated")
    @ApiResponse(responseCode = "409", description = "Invitation expired or you are already part of that team")
    @PostMapping("/invitations/{invID}")
    public ResponseEntity<APIResponse<TeamMemberDTO>> joinTeam(@PathVariable String invID, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        Invitation invitation = invitationService.getInvitationByUUID(invID);
        if (invitation != null && !user.getTeams().contains(invitation.getTeam())){
            invitationService.useInvitation(invitation,user);
            return ResponseEntity.ok(new APIResponse<>("Joined the following team:", new TeamMemberDTO(invitation.getTeam(), user,teamUserRoleService)));
        }
        else{
            throw new ConflictException("Invitation expired or you are already part of that team");
        }
    }
}
