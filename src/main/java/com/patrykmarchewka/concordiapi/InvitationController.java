package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/teams/{teamID}")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;
    @Autowired
    private TeamService teamService;


    @GetMapping("/invitations")
    public ResponseEntity<?> getInvitations(@PathVariable long teamID, Authentication authentication){
        User user = (User)authentication.getPrincipal();
        PublicVariables.UserRole myRole =  teamUserRoleService.getRole(user, teamService.getTeamByID(teamID));
        if (myRole.isOwnerOrAdmin() || myRole.isManager()){
            Set<InvitationManagerDTO> invitations = new HashSet<>();
            for (Invitation inv : invitationService.getAllInvitations(teamService.getTeamByID(teamID))){
                invitations.add(new InvitationManagerDTO(inv,teamUserRoleService));
            }
            return ResponseEntity.ok(new APIResponse<>("List of all invitations for this team:",invitations));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PostMapping("/invitations")
    public ResponseEntity<?> createInvitation(@PathVariable long teamID, @RequestBody @Valid InvitationRequestBody body, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        PublicVariables.UserRole myRole = teamUserRoleService.getRole(user, teamService.getTeamByID(teamID));
        if ((myRole.isOwnerOrAdmin() || myRole.isManager()) && body.getRole().compareTo(myRole) >= 0){
            return ResponseEntity.ok(new APIResponse<>("Created new invitation",new InvitationManagerDTO(invitationService.createInvitation(teamService.getTeamByID(teamID), body),teamUserRoleService)));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PatchMapping("/invitations/{invID}")
    public ResponseEntity<?> patchInvitation(@PathVariable long teamID, @PathVariable String invID, @RequestBody @Valid InvitationRequestBody body, Authentication authentication){
        User user = (User)authentication.getPrincipal();
        PublicVariables.UserRole myRole = teamUserRoleService.getRole(user, teamService.getTeamByID(teamID));
        Invitation invitation = invitationService.getInvitationByUUID(invID);
        if ((myRole.isOwnerOrAdmin() || myRole.isManager()) && invitation != null){
            if (body.getRole() != null){
                invitation.setRole(body.getRole());
            }
            if (body.getUses() != null){
                invitation.setUses(body.getUses());
            }
            if (body.getDueDate() != null){
                invitation.setDueTime(body.getDueDate());
            }
            return ResponseEntity.ok(new APIResponse<>("Patched the invitation",new InvitationManagerDTO(invitationService.saveInvitation(invitation),teamUserRoleService)));
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @DeleteMapping("/invitations/{invID}")
    public ResponseEntity<?> deleteInvitation(@PathVariable long teamID, @PathVariable String invID, Authentication authentication){
        User user = (User)authentication.getPrincipal();
        PublicVariables.UserRole myRole = teamUserRoleService.getRole(user, teamService.getTeamByID(teamID));
        Invitation invitation = invitationService.getInvitationByUUID(invID);
        if ((myRole.isOwnerOrAdmin() || myRole.isManager()) && invitation != null){
            invitationService.deleteInvitation(invitation);
            return ResponseEntity.ok(new APIResponse<>("Invitation has been deleted",null));
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }
}
