package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamAdminDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "BearerAuth")
public class TeamController {
    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;

    @Operation(summary = "Get information about joined teams", description = "Gives information about joined teams based on user role in each team")
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

    @Operation(summary = "Create new team", description = "Creates new team and sets current user as its Owner")
    @PostMapping("/teams")
    public ResponseEntity<?> createTeam(@RequestBody @Valid TeamRequestBody body, Authentication authentication){
        Team team = teamService.createTeam(body.getName(),(User) authentication.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED).body("Team created with ID of " + teamService.getID(team));
    }

    @Operation(summary = "Get information about joined team", description = "Gives information about joined team based on user role")
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

    @Operation(summary = "Change team name", description = "Changes team name to new one")
    @PatchMapping("/teams/{ID}")
    @Transactional
    public ResponseEntity<?> patchTeam(@PathVariable long ID, TeamRequestBody body, Authentication authentication){
        Team team = teamService.getTeamByID(ID);
        if (teamUserRoleService.getRole((User)authentication.getPrincipal(),team).isOwnerOrAdmin()){
            team.setName(body.getName());
            return ResponseEntity.ok(new APIResponse<>("The name has been changed!",body.getName()));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
    }


    @Operation(summary = "Delete the team", description = "Deletes entire team and assosciated data with it")
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
