package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.UserDTO.UserMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
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

    @DeleteMapping("/users/me")
    public ResponseEntity<?> leaveTeam(@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(ID);
        User user = (User)authentication.getPrincipal();
        if(teamUserRoleService.getRole(user,team).equals(PublicVariables.UserRole.ADMIN) && teamUserRoleService.getAllRole(team, PublicVariables.UserRole.ADMIN).size() == 1 && team.getTeammates().size() != 1){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new APIResponse<>("Can't leave team as the only admin, disband team or add new admins",null));
        }
        else{
            teamService.removeUser(team,user);
            return ResponseEntity.ok(new APIResponse<>("Left the team",null));
        }
    }


    @PatchMapping("/users/{ID}/role")
    public ResponseEntity<?> patchUser(@PathVariable long teamID, @PathVariable long ID, @RequestBody PublicVariables.UserRole newRole, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole role = teamUserRoleService.getRole(userService.getUserByID(ID),team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (role.compareTo(myRole) > 0 && newRole.compareTo(myRole) >= 0){
            teamUserRoleService.setRole(userService.getUserByID(ID), team,newRole);
            return ResponseEntity.ok(new APIResponse<>("Role changed",null));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }

    }


}
