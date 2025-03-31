package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.UserMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.Task;
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

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@PathVariable long teamID,Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole role = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        Set<UserMemberDTO> users = new HashSet<>();
        if (role.equals(PublicVariables.UserRole.ADMIN) || role.equals(PublicVariables.UserRole.MANAGER)){
            for (User user : team.getTeammates()){
                users.add(new UserMemberDTO(user));
            }
            return ResponseEntity.ok(users);
        }
        else if(role.equals(PublicVariables.UserRole.MEMBER)){
            return ResponseEntity.ok(team.getTeammates().size());
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @GetMapping("/users/me/refresh")
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

    @GetMapping("/users/{ID}")
    public ResponseEntity<?> getUser(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole role = teamUserRoleService.getRole(userService.getUserByID(ID),team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (role.compareTo(myRole) >= 0){
            return ResponseEntity.ok(new UserMemberDTO(userService.getUserByID(ID)));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }

    }

    //TODO: Add promote/demote, dont use /users/{ID}/promote

    @DeleteMapping("/users/{ID}")
    public ResponseEntity<?> deleteUser(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole role = teamUserRoleService.getRole(userService.getUserByID(ID),team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (role.compareTo(myRole) > 0){
            User user = userService.getUserByID(ID);
            for (Task task : team.getTasks()){
                if (task.getUsers().contains(user)){
                    task.getUsers().remove(user);
                    taskService.saveTask(task);
                }
            }
            team.getTeammates().remove(user);
            teamService.saveTeam(team);
            teamUserRoleService.deleteTMR(teamUserRoleService.getByUserAndTeam(user,team));
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

    }

    @PostMapping("/users/ban/{ID}")
    public ResponseEntity<?> postBanUser(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole role = teamUserRoleService.getRole(userService.getUserByID(ID),team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (role.compareTo(myRole) > 0){
            teamUserRoleService.setRole(userService.getUserByID(ID), team, PublicVariables.UserRole.BANNED);
            return ResponseEntity.ok("User has been banned from team!");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }


}
