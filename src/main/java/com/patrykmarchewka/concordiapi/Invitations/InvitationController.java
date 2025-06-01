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
import org.springframework.web.bind.annotation.*;
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

    @Operation(summary = "Delete invitation", description = "Delete existing invitation for the team")
    @ApiResponse(responseCode = "200", ref = "200")
    @ApiResponse(responseCode = "401", ref = "401")
    @ApiResponse(responseCode = "403", ref = "403")
    @ApiResponse(responseCode = "404", ref = "404")
    @DeleteMapping("/invitations/{invID}")
    public ResponseEntity<APIResponse<Void>> deleteInvitation(@PathVariable long teamID, @PathVariable String invID, Authentication authentication){
        context = context.withUser(authentication).withTeam(teamID).withRole().withInvitation(invID);
        if (!context.getUserRole().isAdminGroup()){
            throw new NoPrivilegesException();
        }
        invitationService.deleteInvitation(context.getInvitation());
        return ResponseEntity.ok(new APIResponse<>("Invitation has been deleted",null));
    }
}
