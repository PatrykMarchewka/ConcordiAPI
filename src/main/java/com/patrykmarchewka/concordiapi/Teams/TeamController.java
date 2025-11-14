package com.patrykmarchewka.concordiapi.Teams;


import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.ControllerContext;
import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.OnPut;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamAdminDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DTO.ValidateGroup;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    /**
     * Returns information about joined team
     * @param authentication User credentials to authenticate
     * @return Message with each Team DTO that user is part of
     */
    @Operation(summary = "Get information about joined teams", description = "Gives information about joined teams based on user role in each team")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @GetMapping("/teams")
    public ResponseEntity<APIResponse<Set<TeamDTO>>> myTeams(Authentication authentication){
        context = context.withUserWithTeams(authentication);
        return ResponseEntity.ok(new APIResponse<>("Information about all joined teams",teamService.getTeamsDTO(context.getUser())));
    }

    /**
     * Creates new team and sets current user as its Owner
     * @param body TeamRequestBody with all the values
     * @param authentication User creating the team
     * @return Message with ID of new created team
     */
    @Operation(summary = "Create new team", description = "Creates new team and sets current user as its Owner")
    @ApiResponse(responseCode = "201", ref = "201")
    @ApiResponse(responseCode = "400", ref = "400")
    @ApiResponse(responseCode = "401", ref = "401")
    @PostMapping("/teams")
    public ResponseEntity<APIResponse<String>> createTeam(@RequestBody @ValidateGroup(OnCreate.class) TeamRequestBody body, Authentication authentication){
        context = context.withUserWithTeams(authentication);
        long teamID = teamService.createTeam(body, context.getUser()).getID();
        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("Team created with ID of " + teamID, null));
    }

    /**
     * Returns information about joined team with provided ID
     * @param ID ID of the team to check for
     * @param authentication User credentials to authenticate
     * @return TeamDTO of team with provided ID
     */
    @Operation(summary = "Get information about joined team", description = "Gives information about joined team based on user role")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/teams/{ID}")
    public ResponseEntity<APIResponse<TeamDTO>> getTeam(@PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication);
        return ResponseEntity.ok(new APIResponse<>("Information about the team", teamService.getTeamDTOByRole(context.getUser().getID(), ID)));
    }

    /**
     * Replaces all team information with new values
     * @param ID ID of the team to edit
     * @param body TeamRequestBody with new team values
     * @param authentication User credentials to authenticate
     * @return TeamAdminDTO of edited Team
     * @throws NoPrivilegesException Thrown when user requesting the change is not Owner or Admin in the team
     */
    @Operation(summary = "Edit team completely", description = "Edits entire Team with all required values")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "400", ref = "400")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PutMapping("/teams/{ID}")
    @Transactional
    public ResponseEntity<APIResponse<TeamAdminDTO>> putTeam(@PathVariable long ID, @RequestBody @ValidateGroup(OnPut.class) TeamRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withRole(ID);

        if(!context.getUserRole().isOwnerOrAdmin()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Team has been edited", new TeamAdminDTO(teamService.putTeam(ID, body))));
    }

    /**
     * Patches team information
     * @param ID ID of team to edit
     * @param body TeamRequestBody with new team values
     * @param authentication User credentials to authenticate
     * @return TeamAdminDTO of edited team
     * @throws NoPrivilegesException Thrown when user requesting the change is not Owner or Admin in the team
     */
    @Operation(summary = "Edit team", description = "Edits team with new values")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "400", ref = "400")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PatchMapping("/teams/{ID}")
    @Transactional
    public ResponseEntity<APIResponse<TeamAdminDTO>> patchTeam(@PathVariable long ID, @RequestBody @ValidateGroup TeamRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withRole(ID);

        if(!context.getUserRole().isOwnerOrAdmin()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Team has been edited",new TeamAdminDTO(teamService.patchTeam(ID, body))));
    }

    /**
     * Deletes team and all information associated with it
     * @param ID ID of the team to delete
     * @param authentication User credentials to authenticate
     * @return Message that team has been disbaned
     * @throws NoPrivilegesException Thrown when user requesting the action is not Owner
     */
    @Operation(summary = "Delete the team", description = "Deletes entire team and associated data with it")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/teams/{ID}")
    @Transactional
    public ResponseEntity<APIResponse<String>> disbandTeam(@PathVariable long ID, Authentication authentication){
        context = context.withUser(authentication).withTeam(ID).withRole();
        if (!context.getUserRole().isOwner()){
            throw new NoPrivilegesException();
        }
        teamService.deleteTeam(context.getTeam());
        return ResponseEntity.ok(new APIResponse<>("The team has been disbanded",null));
    }




}
