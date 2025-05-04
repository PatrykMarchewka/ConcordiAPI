package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/teams/{teamID}")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Users", description = "Managing users in a team")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;
    @Autowired
    private TeamService teamService;

    //param, ?role=ADMIN
    @Operation(summary = "Get users in team", description = "Get users in the team, get their information or just number of teammates depending on your role, you can also filter by role")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@PathVariable long teamID, Authentication authentication, @RequestParam(required = false) PublicVariables.UserRole role){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        Set<UserMemberDTO> users = new HashSet<>();
        if (myRole.isOwnerOrAdmin() || myRole.isManager()){
            if (role != null){
                for (User user : teamUserRoleService.getAllRole(team,role)){
                    users.add(new UserMemberDTO(user));
                }
                return ResponseEntity.ok(new APIResponse<>("All users in the team with that role",users));
            }
            else{
                for (User user : team.getTeammates()){
                    users.add(new UserMemberDTO(user));
                }
                return ResponseEntity.ok(new APIResponse<>("All users in the team",users));
            }
        }
        else if(myRole.isMember()){
            return ResponseEntity.ok(new APIResponse<>("All users in the team",team.getTeammates().size()));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }


    @Operation(summary = "Get information about user", description = "Gets information about user if my role is higher than theirs")
    @GetMapping("/users/{ID}")
    public ResponseEntity<?> getUser(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        try{
            PublicVariables.UserRole role = teamUserRoleService.getRole(userService.getUserByID(ID),team);
            PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
            if (role.compareTo(myRole) >= 0){
                return ResponseEntity.ok(new APIResponse<>("User with the provided ID",new UserMemberDTO(userService.getUserByID(ID))));
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(MenuOptions.CouldntCompleteOperation(),"Can't find user with provided ID in the team"));
        }

    }

    @Operation(summary = "Remove user from team", description = "Removes selected user from team")
    @DeleteMapping("/users/{ID}")
    public ResponseEntity<?> deleteUser(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole role = teamUserRoleService.getRole(userService.getUserByID(ID),team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (role.compareTo(myRole) > 0){
            User user = userService.getUserByID(ID);
            teamService.removeUser(team,user);
            return ResponseEntity.ok("User deleted from team!");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @Operation(summary = "Leave team", description = "Leave the team")
    @DeleteMapping("/users/me")
    public ResponseEntity<?> leaveTeam(@PathVariable long teamID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        User user = (User)authentication.getPrincipal();
        if(teamUserRoleService.getRole(user,team).isOwner() && teamUserRoleService.getAllRole(team, PublicVariables.UserRole.OWNER).size() == 1 && team.getTeammates().size() != 1){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new APIResponse<>("Can't leave team as the only owner, disband team or add new owners",null));
        }
        else{
            teamService.removeUser(team,user);
            return ResponseEntity.ok(new APIResponse<>("Left the team",null));
        }
    }

    @Operation(summary = "Change user role", description = "Change the role of selected user")
    @PatchMapping("/users/{ID}/role")
    public ResponseEntity<?> patchUser(@PathVariable long teamID, @PathVariable long ID, @RequestBody PublicVariables.UserRole newRole, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole role = teamUserRoleService.getRole(userService.getUserByID(ID),team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.isOwnerOrAdmin() && role.compareTo(myRole) > 0 && newRole.compareTo(myRole) >= 0){
            teamUserRoleService.setRole(userService.getUserByID(ID), team,newRole);
            return ResponseEntity.ok(new APIResponse<>("Role changed",null));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }

    }


}
