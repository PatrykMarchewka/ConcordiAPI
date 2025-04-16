package com.example.javaspringbootapi.DTO.InvitationDTO;

import com.example.javaspringbootapi.DTO.TeamDTO.TeamMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.Invitation;
import com.example.javaspringbootapi.PublicVariables;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class InvitationManagerDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String UUID;
    private TeamMemberDTO team;
    private PublicVariables.UserRole role;
    private short uses;
    private String dueTime;

    public InvitationManagerDTO(Invitation invitation){
        this.UUID = invitation.getUUID();
        this.team = new TeamMemberDTO(invitation.getTeam(), null);
        this.role = invitation.getRole();
        this.uses = invitation.getUses();
        this.dueTime = (invitation.getDueTime() != null) ? invitation.getDueTime().toString() : null;
    }

    public InvitationManagerDTO(){}

    @JsonProperty("UUID")
    public String getUUID(){return this.UUID;}
    public void setUUID(String UUID){this.UUID = UUID;}

    public TeamMemberDTO getTeam(){return this.team;}
    public void setTeam(TeamMemberDTO team){this.team = team;}

    public PublicVariables.UserRole getRole(){return this.role;}
    public void setRole(PublicVariables.UserRole role){this.role = role;}

    public short getUses(){return this.uses;}
    public void setUses(short uses){this.uses = uses;}

    public String getDueTime(){return this.dueTime;}
    public void setDueTime(OffsetDateTime dueTime){this.dueTime = (dueTime != null) ? dueTime.toString() : null;}



}
