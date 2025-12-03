package com.patrykmarchewka.concordiapi.DTO.InvitationDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationWithTeam;
import com.patrykmarchewka.concordiapi.UserRole;

import java.time.OffsetDateTime;
import java.util.Objects;

@JsonPropertyOrder({"UUID", "Team", "Role"})
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
    public short getUses() {return uses;}
    @Override
    public void setUses(final short uses) {this.uses = uses;}

    @Override
    public OffsetDateTime getDueTime() {return dueTime;}
    @Override
    public void setDueTime(final OffsetDateTime dueTime) {this.dueTime = dueTime;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof InvitationMemberDTO invitationMemberDTO)) return false;
        return Objects.equals(UUID, invitationMemberDTO.UUID) &&
                Objects.equals(team, invitationMemberDTO.team) &&
                role == invitationMemberDTO.role &&
                uses == invitationMemberDTO.uses &&
                Objects.equals(dueTime, invitationMemberDTO.dueTime);
    }

    @Override
    public int hashCode(){
        return Objects.hash(UUID, team, role, uses, dueTime);
    }
}
