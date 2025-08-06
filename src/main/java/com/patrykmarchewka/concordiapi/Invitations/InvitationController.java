package com.patrykmarchewka.concordiapi.Invitations;

import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.ControllerContext;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.TeamUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/api/teams/{teamID}")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Invitations", description = "Managing invitations to team")
public class InvitationController {

    private final InvitationService invitationService;
    private final TeamUserRoleService teamUserRoleService;
    private ControllerContext context;

    @Autowired
    public InvitationController(InvitationService invitationService, TeamUserRoleService teamUserRoleService, ControllerContext context){
        this.invitationService = invitationService;
        this.teamUserRoleService = teamUserRoleService;
        this.context = context;

    }

    /**
     * Returns information about all invitations generated for the team
     * @param teamID ID of the team to check in
     * @param authentication User credentials to authenticate
     * @return InvitationDTO of all invitations for the team
     * @throws NoPrivilegesException Thrown when user is not Owner,Admin or Manager in the team
     */
    @Operation(summary = "Check invitations",description = "Check all invitations for given team")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/invitations")
    public ResponseEntity<APIResponse<Set<InvitationManagerDTO>>> getInvitations(@PathVariable long teamID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole();

        if (!context.getUserRole().isAdminGroup()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("List of all invitations for this team:",invitationService.getInvitationsDTO(context.getTeam())));
    }

    /**
     * Returns information about specific invitation
     * @param teamID ID of the team to check in
     * @param invID ID of the invitation to check for
     * @param authentication User credentials to authenticate
     * @return InvitationDTO of the specified invitation
     * @throws NoPrivilegesException Thrown when user is not Owner,Admin or Manager in the team
     */
    @Operation(summary = "Check invitation", description = "Check specific invitation for given team")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @GetMapping("/invitations/{invID}")
    public ResponseEntity<APIResponse<InvitationManagerDTO>> getInvitation(@PathVariable long teamID, @PathVariable String invID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withInvitation(invID);

        if (!context.getUserRole().isAdminGroup()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Information about this invitation",new InvitationManagerDTO(context.getInvitation(), teamUserRoleService)));
    }

    /**
     * Generates new invitation
     * @param teamID ID of the team to generate invitation for
     * @param body InvitationRequestBody with information about invitation
     * @param authentication User credentials to authenticate
     * @return InvitationDTO of the generated invitation
     * @throws NoPrivilegesException Thrown when user is not Owner,Admin or Manager in the team or when user tries to generate invitation with higher user role
     */
    @Operation(summary = "Create new invitation",description = "Create new invitation for the team")
    @ApiResponse(responseCode = "201", ref = "201")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PostMapping("/invitations")
    public ResponseEntity<APIResponse<InvitationManagerDTO>> createInvitation(@PathVariable long teamID, @RequestBody @Valid InvitationRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole();
        if (!context.getUserRole().isAdminGroup() || !teamUserRoleService.checkRoles.test(context.getUserRole(),body.getRole())){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("Created new invitation",new InvitationManagerDTO(invitationService.createInvitation(body),teamUserRoleService)));
    }

    /**
     * Replaces all invitation information with new values
     * @param teamID ID of the team to check in
     * @param invID ID of the invitation to edit
     * @param body InvitationRequestBody with new values
     * @param authentication User credentials to authenticate
     * @return InvitationDTO after changes
     * @throws NoPrivilegesException Thrown when user is not Owner,Admin or Manager in the team
     */
    @Operation(summary = "Edit invitation completely", description = "Edits every field on the invitation")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PutMapping("/invitations/{invID}")
    public ResponseEntity<APIResponse<InvitationManagerDTO>> putInvitation(@PathVariable long teamID, @PathVariable String invID, @RequestBody @Valid InvitationRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withInvitation(invID);
        if (!context.getUserRole().isAdminGroup()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Patched the invitation",new InvitationManagerDTO(invitationService.putUpdate(context.getInvitation(), body),teamUserRoleService)));
    }

    /**
     * Patches invitation with new values
     * @param teamID ID of the team to check in
     * @param invID ID of the invitation to edit
     * @param body InvitationRequestBody with new values
     * @param authentication User credentials to authenticate
     * @return InvitationDTO after changes
     * @throws NoPrivilegesException Thrown when user is not Owner,Admin or Manager in the team
     */
    @Operation(summary = "Edit invitation", description = "Edit existing invitation for the team")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @PatchMapping("/invitations/{invID}")
    public ResponseEntity<APIResponse<InvitationManagerDTO>> patchInvitation(@PathVariable long teamID, @PathVariable String invID, @RequestBody @Valid InvitationRequestBody body, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withInvitation(invID);
        if (!context.getUserRole().isAdminGroup()){
            throw new NoPrivilegesException();
        }
        return ResponseEntity.ok(new APIResponse<>("Patched the invitation",new InvitationManagerDTO(invitationService.partialUpdate(context.getInvitation(), body),teamUserRoleService)));
    }

    /**
     * Deletes invitation
     * @param teamID ID of the team to check in
     * @param invID ID of the invitation to delete
     * @param authentication User credentials to authenticate
     * @return Message that invitation has been deleted
     * @throws NoPrivilegesException Thrown when user is not Owner,Admin or Manager in the team
     */
    @Operation(summary = "Delete invitation", description = "Delete existing invitation for the team")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/invitations/{invID}")
    public ResponseEntity<APIResponse<String>> deleteInvitation(@PathVariable long teamID, @PathVariable String invID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withInvitation(invID);
        if (!context.getUserRole().isAdminGroup()){
            throw new NoPrivilegesException();
        }
        invitationService.deleteInvitation(context.getInvitation());
        return ResponseEntity.ok(new APIResponse<>("Invitation has been deleted",null));
    }
}
