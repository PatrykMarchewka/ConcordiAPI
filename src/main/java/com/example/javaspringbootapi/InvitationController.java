package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.InvitationManagerDTO;
import com.example.javaspringbootapi.DTO.InvitationMemberDTO;
import com.example.javaspringbootapi.DTO.InvitationRequestBody;
import com.example.javaspringbootapi.DatabaseModel.Invitation;
import com.example.javaspringbootapi.DatabaseModel.User;
import io.swagger.v3.core.util.AnnotationsUtils;
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
        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER)){
            Set<InvitationManagerDTO> invitations = new HashSet<>();
            for (Invitation inv : invitationService.getAllInvitations(teamService.getTeamByID(teamID))){
                invitations.add(new InvitationManagerDTO(inv));
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
        if ((myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER)) && body.getRole().compareTo(myRole) >= 0){
            return ResponseEntity.ok(new APIResponse<>("Created new invitation:",new InvitationManagerDTO(invitationService.createInvitation(teamService.getTeamByID(teamID), body))));
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
        if ((myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER)) && invitation != null){
            if (body.getRole() != null){
                invitation.setRole(body.getRole());
            }
            if (body.getUses() != null){
                invitation.setUses(body.getUses());
            }
            if (body.getDueDate() != null){
                invitation.setDueTime(body.getDueDate());
            }
            return ResponseEntity.ok(new APIResponse<>("Patched the invitation:",new InvitationManagerDTO(invitationService.saveInvitation(invitation))));
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
        if ((myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER)) && invitation != null){
            invitationService.deleteInvitation(invitation);
            return ResponseEntity.ok("Invitation has been deleted!");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }
}
