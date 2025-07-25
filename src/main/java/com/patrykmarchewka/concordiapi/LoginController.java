package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMeDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestLogin;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.*;
import com.patrykmarchewka.concordiapi.Invitations.InvitationService;
import com.patrykmarchewka.concordiapi.Users.UserService;
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

    private final UserService userService;
    private final TeamUserRoleService teamUserRoleService;
    private final InvitationService invitationService;
    private ControllerContext context;

    @Autowired
    public LoginController(UserService userService, TeamUserRoleService teamUserRoleService, InvitationService invitationService, ControllerContext context){
        this.userService = userService;
        this.teamUserRoleService = teamUserRoleService;
        this.invitationService = invitationService;
        this.context = context;
    }


    /**
     * Checks if the service is working
     * @return ApiResponse if service is up
     */
    @Operation(summary = "Check service status", description = "Checks if service is up and working")
    @ApiResponse(responseCode = "200", ref = "200")
    @GetMapping("/health")
    public ResponseEntity<APIResponse<String>> healthCheck(){
        return ResponseEntity.ok(new APIResponse<>("Service is up!",null));
    }

    /**
     * Generates Json Web Token if provided credentials are correct
     * @param body UserRequestLogin DTO credentials
     * @return ApiResponse with Json Web Token
     * @throws JWTException Thrown when token can't be generated
     */
    @Operation(summary = "Login user", description = "Authenticate the user and return JWT Token")
    @ApiResponse(responseCode = "200",description = "200")
    @ApiResponse(responseCode = "401", description = "401")
    @ApiResponse(responseCode = "409", description = "409")
    @ApiResponse(responseCode = "500", description = "500")
    @PostMapping("/login")
    public ResponseEntity<APIResponse<String>> login(@RequestBody @Valid UserRequestLogin body){
        User user = userService.getUserByLoginAndPassword(body);
        String token = new String();
        try {
            token = JSONWebToken.GenerateJWToken(body.getLogin(),body.getPassword());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new JWTException(e.getMessage(),e);
        }
        return ResponseEntity.ok(new APIResponse<>("Token",token));
    }

    /**
     * Creates new user with specified credentials
     * @param body UserRequestBody credentials
     * @return UserMemberDTO with provided credentials
     */
    @Operation(summary = "Create new user", description = "Create new user with provided credentials")
    @ApiResponse(responseCode = "201", description = "201")
    @ApiResponse(responseCode = "409", description = "409")
    @PostMapping("/signup")
    public ResponseEntity<APIResponse<UserMemberDTO>> create(@RequestBody @Validated(OnCreate.class) UserRequestBody body){
        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("User created",new UserMemberDTO(userService.createUser(body))));
    }

    /**
     * Provides information about logged user
     * @param authentication Authentication from logged user
     * @return UserMeDTO with all information about the account
     */
    @Operation(summary = "Information about me", description = "Return information about currently logged in user")
    @ApiResponse(responseCode = "200", description = "200")
    @ApiResponse(responseCode = "401", description = "401")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/me")
    public ResponseEntity<APIResponse<UserMeDTO>> getMyData(Authentication authentication){
        return ResponseEntity.ok(new APIResponse<>("Data related to my account", new UserMeDTO((User)authentication.getPrincipal(),teamUserRoleService)));
    }

    /**
     * Edits information about the logged user
     * @param body UserRequestBody DTO with all the changes to be applied
     * @param authentication Authentication from logged user
     * @return UserMemberDTO with changed data
     */
    @Operation(summary = "Edit information about me", description = "Edit information about currently logged in user")
    @ApiResponse(responseCode = "200",description = "200")
    @ApiResponse(responseCode = "401", description = "401")
    @ApiResponse(responseCode = "409", description = "409")
    @SecurityRequirement(name = "BearerAuth")
    @PatchMapping("/me")
    @Transactional
    public ResponseEntity<APIResponse<UserMemberDTO>> patchUser(@RequestBody UserRequestBody body, Authentication authentication){
        context = context.withUser(authentication);
        userService.patchUser(context.getUser(), body);
        return ResponseEntity.ok(new APIResponse<>("Data changed!",new UserMemberDTO(context.getUser())));
    }

    /**
     * Generates new Json Web Token that is valid for 1 hour
     * @param authentication Authentication from logged user
     * @return Json Web Token that is valid for 1 hour
     * @throws JWTException Thrown when token can't be generated
     */
    @Operation(summary = "Generate new token", description = "Generates new JWT token")
    @ApiResponse(responseCode = "200",description = "200")
    @ApiResponse(responseCode = "401", description = "401")
    @ApiResponse(responseCode = "500", description = "500")
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/me/refresh")
    public ResponseEntity<APIResponse<String>> refreshToken(Authentication authentication){
        context = context.withUser(authentication);
        String response;
        try {
            response = JSONWebToken.GenerateJWToken(context.getUser().getLogin(),context.getUser().getPassword());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new JWTException(e.getMessage(),e);
        }
        return ResponseEntity.ok(new APIResponse<>("Your new token",response));
    }

    /**
     * Provides information about Invitation and potential team to join
     * @param invID Invitation UUID
     * @return InvitationMemberDTO regarding the invitation and team
     * @throws NotFoundException Thrown when invitation with the provided UUID can't be found
     */
    @Operation(summary = "Check invitation", description = "Returns information about provided invitation")
    @ApiResponse(responseCode = "200", description = "200")
    @ApiResponse(responseCode = "401", description = "401")
    @ApiResponse(responseCode = "404", description = "404")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/invitations/{invID}")
    public ResponseEntity<APIResponse<InvitationMemberDTO>> getInfoAboutInvitation(@PathVariable String invID){
        context = context.withInvitation(invID);
        if (context.getInvitation() != null){
            return ResponseEntity.ok(new APIResponse<>("The provided invitation information",new InvitationMemberDTO(context.getInvitation(), teamUserRoleService)));
        }
        else{
            throw new NotFoundException();
        }
    }

    /**
     * Joins the team and returns information about it
     * @param invID Invitation UUID
     * @param authentication Authentication from logged user
     * @return TeamMemberDTO with the joined team
     * @throws ConflictException Thrown when invitation is expired or user is already part of the team
     */
    @Operation(summary = "Join team using invitation", description = "Joins team using the provided invitation")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponse(responseCode = "200", description = "200")
    @ApiResponse(responseCode = "401", description = "401")
    @ApiResponse(responseCode = "409", description = "409")
    @PostMapping("/invitations/{invID}")
    public ResponseEntity<APIResponse<TeamMemberDTO>> joinTeam(@PathVariable String invID, Authentication authentication) throws Exception {
        context = context.withUser(authentication).withInvitation(invID);
        if (context.getInvitation() != null && !context.getUser().checkTeam(context.getTeam())){
            invitationService.useInvitation(context.getInvitation(), context.getUser());
            return ResponseEntity.ok(new APIResponse<>("Joined the following team:", new TeamMemberDTO(context.getTeam(), context.getUser(), teamUserRoleService)));
        }
        else{
            throw new ConflictException("Invitation expired or you are already part of that team");
        }
    }
}
