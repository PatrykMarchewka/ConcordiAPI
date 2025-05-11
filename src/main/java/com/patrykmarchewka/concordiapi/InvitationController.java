package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/teams/{teamID}")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Invitations", description = "Managing invitations to team")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;
    @Autowired
    private TeamService teamService;


    @Operation(summary = "Check invitations",description = "Check all invitations for given team")
    @ApiResponse(responseCode = "200",description = "Data about invitations was provided")
    @ApiResponse(responseCode = "401", description = "You are not authenticated")
    @ApiResponse(responseCode = "403", description = "You don't have enough privileges to perform that action")
    @GetMapping("/invitations")
    public ResponseEntity<APIResponse<Set<InvitationManagerDTO>>> getInvitations(@PathVariable long teamID, Authentication authentication){
        if (!teamUserRoleService.getRole((User)authentication.getPrincipal(), teamService.getTeamByID(teamID)).isAdminGroup()){
            throw new NoPrivilegesException();
        }

        return ResponseEntity.ok(new APIResponse<>("List of all invitations for this team:",invitationService.getInvitationsDTO(teamService.getTeamByID(teamID))));
    }

    @Operation(summary = "Create new invitation",description = "Create new invitation for the team")
    @ApiResponse(responseCode = "201",description = "Created new invitation")
    @ApiResponse(responseCode = "401", description = "You are not authenticated")
    @ApiResponse(responseCode = "403", description = "You don't have enough privileges to perform that action")
    @PostMapping("/invitations")
    public ResponseEntity<APIResponse<InvitationManagerDTO>> createInvitation(@PathVariable long teamID, @RequestBody @Valid InvitationRequestBody body, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        PublicVariables.UserRole myRole = teamUserRoleService.getRole(user, teamService.getTeamByID(teamID));
        if (!myRole.isAdminGroup() || body.getRole().compareTo(myRole) < 0){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("Created new invitation",new InvitationManagerDTO(invitationService.createInvitation(teamService.getTeamByID(teamID), body),teamUserRoleService)));
    }

    @Operation(summary = "Edit invitation", description = "Edit existing invitation for the team")
    @ApiResponse(responseCode = "200",description = "Invitation was edited")
    @ApiResponse(responseCode = "401", description = "You are not authenticated")
    @ApiResponse(responseCode = "403", description = "You don't have enough privileges to perform that action")
    @PatchMapping("/invitations/{invID}")
    public ResponseEntity<?> patchInvitation(@PathVariable long teamID, @PathVariable String invID, @RequestBody @Valid InvitationRequestBody body, Authentication authentication){
        if (invitationService.getInvitationByUUID(invID) == null){
            throw new NotFoundException();
        }
        else if (!teamUserRoleService.getRole((User)authentication.getPrincipal(), teamService.getTeamByID(teamID)).isAdminGroup()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Patched the invitation",new InvitationManagerDTO(invitationService.partialUpdate(invitationService.getInvitationByUUID(invID), body),teamUserRoleService)));
    }

    @Operation(summary = "Delete invitation", description = "Delete existing invitation for the team")
    @ApiResponse(responseCode = "200",description = "Invitation has been deleted")
    @ApiResponse(responseCode = "401", description = "You are not authenticated")
    @ApiResponse(responseCode = "403", description = "You don't have enough privileges to perform that action")
    @ApiResponse(responseCode = "404",description = "Invitation was not found")
    @DeleteMapping("/invitations/{invID}")
    public ResponseEntity<APIResponse<Void>> deleteInvitation(@PathVariable long teamID, @PathVariable String invID, Authentication authentication){
        if (invitationService.getInvitationByUUID(invID) == null){
            throw new NotFoundException();
        }
        else if(!teamUserRoleService.getRole((User)authentication.getPrincipal(), teamService.getTeamByID(teamID)).isAdminGroup()){
            throw new NoPrivilegesException();
        }
        invitationService.deleteInvitation(invitationService.getInvitationByUUID(invID));
        return ResponseEntity.ok(new APIResponse<>("Invitation has been deleted",null));
    }
}
