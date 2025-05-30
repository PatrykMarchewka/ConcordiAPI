package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.*;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.*;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Teams", description = "Managing teams")
public class TeamController {
    private final TeamService teamService;
    private ControllerContext context;

    @Autowired
    public TeamController(TeamService teamService, ControllerContext context){
        this.teamService = teamService;
        this.context = context;
    }

    @Operation(summary = "Get information about joined teams", description = "Gives information about joined teams based on user role in each team")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/teams")
    public ResponseEntity<?> myTeams(Authentication authentication){
        context = context.withUser(authentication);
        return ResponseEntity.ok(new APIResponse<>("Information about all joined teams",teamService.getTeamsDTO(context.getUser())));
    }

    @Operation(summary = "Create new team", description = "Creates new team and sets current user as its Owner")
    @ApiResponse(responseCode = "201", ref = "201")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "404", ref = "404")
    @PostMapping("/teams")
    public ResponseEntity<?> createTeam(@RequestBody @Valid TeamRequestBody body, Authentication authentication){
        Team team = teamService.createTeam(body,(User) authentication.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED).body("Team created with ID of " + teamService.getID(team));
    }

    @Operation(summary = "Get information about joined team", description = "Gives information about joined team based on user role")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/teams/{ID}")
    public ResponseEntity<?> getTeam(@PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(ID).withRole();
        if (!context.getUserRole().isAllowedBasic()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Information about the team", teamService.createTeamDTO(context.getUser(), context.getTeam())));
    }

    @Operation(summary = "Change team name", description = "Changes team name to new one")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PatchMapping("/teams/{ID}")
    @Transactional
    public ResponseEntity<?> patchTeam(@PathVariable long ID, TeamRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(ID).withRole();

        if(!context.getUserRole().isOwnerOrAdmin()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Team has been edited",teamService.createTeamDTO(context.getUser(), teamService.patchTeam(context.getTeam(), body))));
    }


    @Operation(summary = "Delete the team", description = "Deletes entire team and assosciated data with it")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/teams/{ID}")
    @Transactional
    public ResponseEntity<?> disbandTeam(@PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(ID).withRole();
        if (!context.getUserRole().isOwner()){
            throw new NoPrivilegesException();
        }
        teamService.removeAllUsers(context.getTeam());
        return ResponseEntity.ok(new APIResponse<>("The team has been disbanded",null));
    }




}
