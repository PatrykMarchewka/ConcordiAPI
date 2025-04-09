package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Invitation;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.PublicVariables;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class InvitationManagerDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String UUID;
    private Team team;
    private PublicVariables.UserRole role;
    private short uses;
    private ZonedDateTime dueTime;

    public InvitationManagerDTO(Invitation invitation){
        this.UUID = invitation.getUUID();
        this.team = invitation.getTeam();
        this.role = invitation.getRole();
        this.uses = invitation.getUses();
        this.dueTime = invitation.getDueTime();
    }

    public InvitationManagerDTO(){}

    public String getUUID(){return this.UUID;}
    public void setUUID(String UUID){this.UUID = UUID;}

    public Team getTeam(){return this.team;}
    public void setTeam(Team team){this.team = team;}

    public PublicVariables.UserRole getRole(){return this.role;}
    public void setRole(PublicVariables.UserRole role){this.role = role;}

    public short getUses(){return this.uses;}
    public void setUses(short uses){this.uses = uses;}

    public ZonedDateTime getDueTime(){return this.dueTime;}
    public void setDueTime(ZonedDateTime dueTime){this.dueTime = dueTime;}



}
