package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.*;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    //param, ?role=ADMIN
    @Operation(summary = "Get users in team", description = "Get users in the team, get their information or just number of teammates depending on your role, you can also filter by role")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@PathVariable long teamID, Authentication authentication, @RequestParam(required = false) UserRole role){
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


    @Operation(summary = "Get information about user", description = "Gets information about user if my role is higher than theirs")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/users/{ID}")
    public ResponseEntity<?> getUser(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withOtherRole(userService.getUserByID(ID));
        if (!teamUserRoleService.checkRoles.test(context.getUserRole(),context.getOtherRole())){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("User with the provided ID",new UserMemberDTO(userService.getUserByID(ID))));
    }

    @Operation(summary = "Remove user from team", description = "Removes selected user from team")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/users/{ID}")
    public ResponseEntity<?> deleteUser(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withOtherRole(userService.getUserByID(ID));

        if (teamUserRoleService.checkRoles(context.getUserRole(),context.getOtherRole()) <= 0){
            throw new NoPrivilegesException();
        }
        teamService.removeUser(context.getTeam(), userService.getUserByID(ID));
        return ResponseEntity.ok("User deleted from team!");
    }

    @Operation(summary = "Leave team", description = "Leave the team")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/users/me")
    public ResponseEntity<?> leaveTeam(@PathVariable long teamID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole();

        if (context.getUserRole().isOwner() && teamUserRoleService.getAllByTeamAndUserRole(context.getTeam(), UserRole.OWNER).size() == 1 && context.getTeam().getTeammates().size() != 1){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new APIResponse<>("Can't leave team as the only owner, disband team or add new owners",null));
        }
        else{
            teamService.removeUser(context.getTeam(), context.getUser());
            return ResponseEntity.ok(new APIResponse<>("Left the team",null));
        }
    }

    @Operation(summary = "Change user role", description = "Change the role of selected user")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PatchMapping("/users/{ID}/role")
    public ResponseEntity<?> patchUser(@PathVariable long teamID, @PathVariable long ID, @RequestBody UserRole newRole, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withOtherRole(userService.getUserByID(ID));
        if (!context.getUserRole().isOwnerOrAdmin() || teamUserRoleService.checkRoles(context.getUserRole(),context.getOtherRole()) <= 0 || !teamUserRoleService.checkRoles.test(newRole,context.getUserRole())){
            throw new NoPrivilegesException();
        }
        teamUserRoleService.setRole(userService.getUserByID(ID), context.getTeam(), newRole);
        return ResponseEntity.ok(new APIResponse<>("Role changed",null));

    }


}
