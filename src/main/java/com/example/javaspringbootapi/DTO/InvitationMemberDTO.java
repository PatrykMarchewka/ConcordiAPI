package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Invitation;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.PublicVariables;

public class InvitationMemberDTO {
    private String UUID;
    private Team team;
    private PublicVariables.UserRole role;

    public InvitationMemberDTO(Invitation invitation){
        this.UUID = invitation.getUUID();
        this.team = invitation.getTeam();
        this.role = invitation.getRole();
    }

    public InvitationMemberDTO(){}

    public String getUUID(){return this.UUID;}
    public void setUUID(String UUID){this.UUID = UUID;}

    public Team getTeam(){return this.team;}
    public void setTeam(Team team){this.team = team;}

    public PublicVariables.UserRole getRole(){return this.role;}
    public void setRole(PublicVariables.UserRole role){this.role = role;}
}
