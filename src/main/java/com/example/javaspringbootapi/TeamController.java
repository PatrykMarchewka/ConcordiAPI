package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.TeamDTO.TeamAdminDTO;
import com.example.javaspringbootapi.DTO.TeamDTO.TeamManagerDTO;
import com.example.javaspringbootapi.DTO.TeamDTO.TeamMemberDTO;
import com.example.javaspringbootapi.DTO.TeamDTO.TeamRequestBody;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
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
            if (role.isOwnerOrAdmin()){
                teams.add(new TeamAdminDTO(team,teamUserRoleService));
            }
            else if(role.isManager()){
                teams.add(new TeamManagerDTO(team,teamUserRoleService));
            }
            else if(role.isMember()){
                teams.add(new TeamMemberDTO(team,user,teamUserRoleService));
            }
        }
        return ResponseEntity.ok(new APIResponse<>("Information about all joined teams",teams));
    }
    @PostMapping("/teams")
    public ResponseEntity<?> createTeam(@RequestBody @Valid TeamRequestBody body, Authentication authentication){
        Team team = teamService.createTeam(body.getName(),(User) authentication.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED).body("Team created with ID of " + teamService.getID(team));
    }

    @GetMapping("/teams/{ID}")
    public ResponseEntity<?> getTeam(@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(ID);
        PublicVariables.UserRole role = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (role.isOwnerOrAdmin()){
            return ResponseEntity.ok(new APIResponse<>("Information about the team",new TeamAdminDTO(team,teamUserRoleService)));
        }
        else if(role.isManager()){
            return ResponseEntity.ok(new APIResponse<>("Information about the team",new TeamManagerDTO(team,teamUserRoleService)));
        }
        else if(role.isMember()){
            return ResponseEntity.ok(new APIResponse<>("Information about the team",new TeamMemberDTO(team,(User)authentication.getPrincipal(),teamUserRoleService)));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }


    @DeleteMapping("/teams/{ID}")
    @Transactional
    public ResponseEntity<?> disbandTeam(@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(ID);
        User user = (User)authentication.getPrincipal();
        if(teamUserRoleService.getRole(user,team).isOwner()){
            for (User user1 : team.getTeammates()){
                teamService.removeUser(team,user1);
            }
            return ResponseEntity.ok(new APIResponse<>("The team has been disbanded",null));
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }
    }




}
