package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.ControllerContext;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/teams/{teamID}")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Users", description = "Managing users in a team")
public class UserController {

    private final UserService userService;
    private final TeamUserRoleService teamUserRoleService;
    private final TeamService teamService;
    private ControllerContext context;

    @Autowired
    public UserController(UserService userService, TeamUserRoleService teamUserRoleService, TeamService teamService, ControllerContext context){
        this.userService = userService;
        this.teamUserRoleService = teamUserRoleService;
        this.teamService = teamService;
        this.context = context;
    }

    /**
     * Gets information about users in team
     * @param teamID Team ID to check in
     * @param authentication Authentication from logged user
     * @param role Optional, UserRole in String form
     * @return UserMemberDTO information about users if user has sufficient privileges, otherwise throws
     * @throws NoPrivilegesException Thrown when user is not in admin group
     */
    //param, ?role=ADMIN
    @Operation(summary = "Get users in team", description = "Get users in the team with their information, allows filtering by role with optional param")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/users")
    public ResponseEntity<APIResponse<Set<UserMemberDTO>>> getAllUsers(@PathVariable long teamID, Authentication authentication, @RequestParam(required = false) UserRole role){
        context = context.withUser(authentication).withTeam(teamID).withRole();
        if (!context.getUserRole().isAdminGroup()){
            throw new NoPrivilegesException();
        }

        if (role != null){
            return ResponseEntity.ok(new APIResponse<>("All users in the team with that role",userService.userMemberDTOSetParam(context.getUserRole(),role, context.getTeam())));
        }
        else{
            return ResponseEntity.ok(new APIResponse<>("All users in the team",userService.userMemberDTOSetNoParam(context.getUserRole(), context.getTeam())));
        }
    }


    /**
     * Gets information about specific user
     * @param teamID Team ID of which both users are part of
     * @param ID ID of the user you want to check
     * @param authentication Authentication from logged user
     * @return Information about user with given ID
     * @throws NoPrivilegesException Thrown when you don't have enough privileges to get information about other person
     */
    @Operation(summary = "Get information about user", description = "Gets information about user if my role is same or higher than theirs")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/users/{ID}")
    public ResponseEntity<APIResponse<UserMemberDTO>> getUser(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withOtherRole(userService.getUserByID(ID));

        teamUserRoleService.forceCheckRoles(context.getUserRole(), context.getOtherRole());

        return ResponseEntity.ok(new APIResponse<>("User with the provided ID",new UserMemberDTO(userService.getUserByID(ID))));
    }

    /**
     * Removes given user from Team
     * @param teamID Team ID from which you want to kick the user
     * @param ID ID of the user you want to kick
     * @param authentication Authentication from logged user
     * @return Message if user has been removed successfully from the team
     * @throws NoPrivilegesException Thrown if other user has same or higher role than user kicking
     */
    @Operation(summary = "Remove user from team", description = "Removes selected user from team")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/users/{ID}")
    public ResponseEntity<APIResponse<String>> deleteUser(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withOtherRole(userService.getUserByID(ID));

        teamUserRoleService.forceCheckRoles(context.getUserRole(), context.getOtherRole());

        teamService.removeUser(teamID, userService.getUserByID(ID));
        return ResponseEntity.ok(new APIResponse<>("User removed from team",null));
    }

    /**
     * Leaves the team
     * @param teamID Team ID to leave
     * @param authentication Authentication from logged user
     * @return Message whether team was left successfully or were there any problems
     */
    @Operation(summary = "Leave team", description = "Leave the team")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/users/me")
    public ResponseEntity<APIResponse<String>> leaveTeam(@PathVariable long teamID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole();

        if (context.getUserRole().isOwner() && teamUserRoleService.getAllByTeamAndUserRole(context.getTeam(), UserRole.OWNER).size() == 1 && context.getTeam().getTeammates().size() != 1){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new APIResponse<>("Can't leave team as the only owner, disband team or add new owners",null));
        }
        else{
            teamService.removeUser(teamID, context.getUser());
            return ResponseEntity.ok(new APIResponse<>("Left the team",null));
        }
    }

    /**
     * Changes the UserRole of a specified User in a team
     * @param teamID Team ID in which both users are part of
     * @param ID ID of user you want to change role of
     * @param newRole New role that you want to give
     * @param authentication Authentication from logged user
     * @return Message if role was successfully changed
     * @throws NoPrivilegesException Thrown when User changing role is not Owner or Admin or when new role is higher than the one of User changing role
     */
    @Operation(summary = "Change user role", description = "Change the role of selected user")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PatchMapping("/users/{ID}/role")
    public ResponseEntity<APIResponse<String>> patchUser(@PathVariable long teamID, @PathVariable long ID, @RequestBody UserRole newRole, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole();
        if (!context.getUserRole().isOwnerOrAdmin()){
            throw new NoPrivilegesException();
        }
        teamUserRoleService.setRole(context.getUserRole(), ID, teamID, newRole);
        return ResponseEntity.ok(new APIResponse<>("Role changed",null));

    }


}
