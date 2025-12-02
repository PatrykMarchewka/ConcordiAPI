package com.patrykmarchewka.concordiapi.DTO.InvitationDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationWithTeam;
import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.UserRole;

import java.time.OffsetDateTime;
import java.util.Objects;

@JsonPropertyOrder({"UUID", "Team", "Role", "Uses", "Due time"})
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

    @Override
    @JsonProperty("UUID")
    public String getUUID(){return this.UUID;}
    @Override
    public void setUUID(String UUID){this.UUID = UUID;}

    @JsonProperty("Team")
    public TeamMemberDTO getTeam(){return this.team;}
    public void setTeam(TeamMemberDTO team){this.team = team;}

    @Override
    @JsonProperty("Role")
    public UserRole getRole(){return this.role;}
    @Override
    public void setRole(UserRole role){this.role = role;}

    @Override
    @JsonProperty("Uses")
    public short getUses(){return this.uses;}
    @Override
    public void setUses(short uses){this.uses = uses;}

    @Override
    public OffsetDateTime getDueTime(){return this.dueTime;}
    @Override
    public void setDueTime(OffsetDateTime dueTime){this.dueTime = dueTime;}

    @JsonProperty("Due time")
    public String getDueTimeString(){return OffsetDateTimeConverter.formatDate(this.dueTime);}
    public void setDueTimeString(String dueTimeString){ this.dueTime = OffsetDateTimeConverter.parseDate(dueTimeString);}


    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof InvitationManagerDTO invitationManagerDTO)) return false;
        return Objects.equals(UUID, invitationManagerDTO.UUID) &&
                Objects.equals(team, invitationManagerDTO.team) &&
                Objects.equals(role, invitationManagerDTO.role) &&
                uses == invitationManagerDTO.uses &&
                Objects.equals(dueTime, invitationManagerDTO.dueTime);
    }

    @Override
    public int hashCode(){
        return Objects.hash(UUID, team, role, uses, dueTime);
    }
}
