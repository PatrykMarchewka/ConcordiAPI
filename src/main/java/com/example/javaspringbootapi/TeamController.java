package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.TeamAdminDTO;
import com.example.javaspringbootapi.DTO.TeamManagerDTO;
import com.example.javaspringbootapi.DTO.TeamMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class TeamController {
    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;

    @GetMapping("/teams")
    public ResponseEntity<?> myTeams(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        Set<Object> teams = new HashSet<>();
        for (Team team : user.getTeams()){
            PublicVariables.UserRole role = teamUserRoleService.getRole(user,team);
            if (role.equals(PublicVariables.UserRole.ADMIN)){
                teams.add(new TeamAdminDTO(team,teamUserRoleService));
            }
            else if(role.equals(PublicVariables.UserRole.MANAGER)){
                teams.add(new TeamManagerDTO(team,teamUserRoleService));
            }
            else if(role.equals(PublicVariables.UserRole.MEMBER)){
                teams.add(new TeamMemberDTO(team,user));
            }
        }
        return ResponseEntity.ok(teams);
    }

    @PostMapping("/teams")
    public ResponseEntity<?> createTeam(@RequestBody Map<String,String> body, Authentication authentication){
        Team team = teamService.createTeam(body.get("name"),(User) authentication.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED).body("Team created with ID of " + teamService.getID(team));
    }

    @GetMapping("/teams/{ID}")
    public ResponseEntity<?> getTeam(@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(ID);
        PublicVariables.UserRole role = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (role.equals(PublicVariables.UserRole.ADMIN)){
            return ResponseEntity.ok(new TeamAdminDTO(team,teamUserRoleService));
        }
        else if(role.equals(PublicVariables.UserRole.MANAGER)){
            return ResponseEntity.ok(new TeamManagerDTO(team,teamUserRoleService));
        }
        else if(role.equals(PublicVariables.UserRole.MEMBER)){
            return ResponseEntity.ok(new TeamMemberDTO(team,(User)authentication.getPrincipal()));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }





}
