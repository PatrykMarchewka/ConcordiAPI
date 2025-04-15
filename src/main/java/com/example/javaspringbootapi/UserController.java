package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.UserMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.TeamUserRole;
import com.example.javaspringbootapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/teams/{teamID}")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private TaskService taskService;

    //param, ?role=ADMIN
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@PathVariable long teamID, Authentication authentication, @RequestParam(required = false) PublicVariables.UserRole role){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole myrole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        Set<UserMemberDTO> users = new HashSet<>();
        if (myrole.equals(PublicVariables.UserRole.ADMIN) || myrole.equals(PublicVariables.UserRole.MANAGER)){
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
        else if(role.equals(PublicVariables.UserRole.MEMBER)){
            return ResponseEntity.ok(new APIResponse<>("All users in the team",team.getTeammates().size()));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }



    @GetMapping("/users/{ID}")
    public ResponseEntity<?> getUser(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole role = teamUserRoleService.getRole(userService.getUserByID(ID),team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (role.compareTo(myRole) >= 0){
            return ResponseEntity.ok(new APIResponse<>("User with the provided ID",new UserMemberDTO(userService.getUserByID(ID))));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }

    }

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

    @GetMapping("/users/ban")
    public ResponseEntity<?> getBannedUsers(@PathVariable long teamID,Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if ( myRole.equals(PublicVariables.UserRole.ADMIN)|| myRole.equals(PublicVariables.UserRole.MANAGER)){
            Set<UserMemberDTO> usersDTO = new HashSet<>();
            for (User user : teamUserRoleService.getAllRole(team, PublicVariables.UserRole.BANNED)){
                usersDTO.add(new UserMemberDTO(user));
            }
            return ResponseEntity.ok(usersDTO);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }


    @PatchMapping("/users/{ID}/role")
    public ResponseEntity<?> patchUser(@PathVariable long teamID, @PathVariable long ID, @RequestBody PublicVariables.UserRole newRole, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole role = teamUserRoleService.getRole(userService.getUserByID(ID),team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (role.compareTo(myRole) > 0 && newRole.compareTo(myRole) >= 0){
            teamUserRoleService.setRole(userService.getUserByID(ID), team,newRole);
            return ResponseEntity.ok("Role changed!");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }

    }


}
