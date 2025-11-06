package com.patrykmarchewka.concordiapi.DTO.InvitationDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationWithTeam;
import com.patrykmarchewka.concordiapi.UserRole;

import java.time.OffsetDateTime;

public class InvitationMemberDTO implements InvitationDTO{
    private String UUID;
    private TeamMemberDTO team;
    private UserRole role;
    @JsonIgnore
    private short uses;
    @JsonIgnore
    private OffsetDateTime dueTime;

    public InvitationMemberDTO(InvitationWithTeam invitation){
        this.UUID = invitation.getUUID();
        this.team = new TeamMemberDTO(invitation.getInvitingTeam());
        this.role = invitation.getRole();
        this.uses = invitation.getUses();
        this.dueTime = invitation.getDueTime();
    }

    public InvitationMemberDTO(){}

    @Override
    public String getUUID(){return this.UUID;}
    public void setUUID(String UUID){this.UUID = UUID;}

    public TeamMemberDTO getTeam(){return this.team;}
    public void setTeam(TeamMemberDTO team){this.team = team;}

    @Override
    public UserRole getRole(){return this.role;}
    public void setRole(UserRole role){this.role = role;}

    @Override
    public short getUses() {return uses;}
    public void setUses(final short uses) {this.uses = uses;}

    @Override
    public OffsetDateTime getDueTime() {return dueTime;}
    public void setDueTime(final OffsetDateTime dueTime) {this.dueTime = dueTime;}
}
