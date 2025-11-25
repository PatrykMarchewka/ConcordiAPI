package com.patrykmarchewka.concordiapi.DTO.InvitationDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationWithTeam;
import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.UserRole;

import java.time.OffsetDateTime;
import java.util.Objects;

public class InvitationManagerDTO implements InvitationDTO{

    private String UUID;
    private TeamMemberDTO team;
    private UserRole role;
    private short uses;
    @JsonIgnore
    private OffsetDateTime dueTime;

    public InvitationManagerDTO(InvitationWithTeam invitation){
        this.UUID = invitation.getUUID();
        this.team = new TeamMemberDTO(invitation.getInvitingTeam());
        this.role = invitation.getRole();
        this.uses = invitation.getUses();
        this.dueTime = invitation.getDueTime();
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

    public OffsetDateTime getDueTime(){return this.dueTime;}
    public void setDueTime(OffsetDateTime dueTime){this.dueTime = dueTime;}

    @JsonProperty("dueTime")
    public String getDueTimeString(){return OffsetDateTimeConverter.formatDate(this.dueTime);}


    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof InvitationManagerDTO invitationManagerDTO)) return false;
        return Objects.equals(UUID, invitationManagerDTO.getUUID()) &&
                Objects.equals(role, invitationManagerDTO.getRole()) &&
                Objects.equals(uses, invitationManagerDTO.getUses()) &&
                Objects.equals(dueTime, invitationManagerDTO.getDueTime());
    }

    @Override
    public int hashCode(){
        return Objects.hash(UUID);
    }
}
