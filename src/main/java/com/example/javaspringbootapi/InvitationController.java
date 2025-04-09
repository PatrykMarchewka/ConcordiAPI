package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.InvitationManagerDTO;
import com.example.javaspringbootapi.DatabaseModel.Invitation;
import com.example.javaspringbootapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Map;
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
            return ResponseEntity.ok(invitations);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PostMapping("/invitations")
    public ResponseEntity<?> createInvitation(@PathVariable long teamID,@RequestBody InvitationManagerDTO body, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        PublicVariables.UserRole myRole = teamUserRoleService.getRole(user, teamService.getTeamByID(teamID));
        if ((myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER)) && body.getRole().compareTo(myRole) >= 0){
            return ResponseEntity.ok(invitationService.createInvitation(body));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PatchMapping("/invitations/{invID}")
    public ResponseEntity<?> patchInvitation(@PathVariable long teamID, @PathVariable String invID, @RequestBody Map<String,Object> body, Authentication authentication){
        User user = (User)authentication.getPrincipal();
        PublicVariables.UserRole myRole = teamUserRoleService.getRole(user, teamService.getTeamByID(teamID));
        Invitation invitation = invitationService.getInvitationByUUID(invID);
        if ((myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER)) && invitation != null){
            if (body.containsKey("uuid") || body.containsKey("team")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't patch sensitive fields!");
            }
            if (body.containsKey("role")){
                invitation.setRole(PublicVariables.UserRole.fromString(body.get("role").toString()));
            }
            if (body.containsKey("uses")){
                invitation.setUses(Short.valueOf(body.get("uses").toString()));
            }
            if (body.containsKey("dueTime")){
                invitation.setDueTime((ZonedDateTime) body.get("dueTime"));
            }
            invitationService.saveInvitation(invitation);
            return ResponseEntity.ok(new InvitationManagerDTO(invitation));
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
