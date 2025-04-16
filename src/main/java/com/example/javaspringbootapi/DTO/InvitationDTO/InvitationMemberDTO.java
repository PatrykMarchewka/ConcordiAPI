package com.example.javaspringbootapi.DTO.InvitationDTO;

import com.example.javaspringbootapi.DTO.TeamDTO.TeamMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.Invitation;
import com.example.javaspringbootapi.PublicVariables;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvitationMemberDTO {
    private String UUID;
    private TeamMemberDTO team;
    private PublicVariables.UserRole role;

    public InvitationMemberDTO(Invitation invitation){
        this.UUID = invitation.getUUID();
        this.team = new TeamMemberDTO(invitation.getTeam(),null);
        this.role = invitation.getRole();
    }

    public InvitationMemberDTO(){}

    @JsonProperty("UUID")
    public String getUUID(){return this.UUID;}
    public void setUUID(String UUID){this.UUID = UUID;}

    public TeamMemberDTO getTeam(){return this.team;}
    public void setTeam(TeamMemberDTO team){this.team = team;}

    public PublicVariables.UserRole getRole(){return this.role;}
    public void setRole(PublicVariables.UserRole role){this.role = role;}
}
