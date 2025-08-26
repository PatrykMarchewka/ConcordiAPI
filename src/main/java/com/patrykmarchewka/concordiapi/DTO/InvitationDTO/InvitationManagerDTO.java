package com.patrykmarchewka.concordiapi.DTO.InvitationDTO;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.UserRole;

import java.time.OffsetDateTime;

public class InvitationManagerDTO {

    private String UUID;
    private TeamMemberDTO team;
    private UserRole role;
    private short uses;
    private String dueTime;

    public InvitationManagerDTO(Invitation invitation, TeamUserRoleService teamUserRoleService){
        this.UUID = invitation.getUUID();
        this.team = new TeamMemberDTO(invitation.getInvitingTeam(), null,teamUserRoleService);
        this.role = invitation.getRole();
        this.uses = invitation.getUses();
        this.dueTime = (invitation.getDueTime() != null) ? invitation.getDueTime().toString() : null;
    }

    public InvitationManagerDTO(){}

    public String getUUID(){return this.UUID;}
    public void setUUID(String UUID){this.UUID = UUID;}

    public TeamMemberDTO getTeam(){return this.team;}
    public void setTeam(TeamMemberDTO team){this.team = team;}

    public UserRole getRole(){return this.role;}
    public void setRole(UserRole role){this.role = role;}

    public short getUses(){return this.uses;}
    public void setUses(short uses){this.uses = uses;}

    public String getDueTime(){return this.dueTime;}
    public void setDueTime(OffsetDateTime dueTime){this.dueTime = (dueTime != null) ? dueTime.toString() : null;}



}
