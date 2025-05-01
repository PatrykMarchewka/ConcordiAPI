package com.patrykmarchewka.concordiapi.DTO.InvitationDTO;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.PublicVariables;
import com.patrykmarchewka.concordiapi.TeamUserRoleService;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvitationMemberDTO {
    private String UUID;
    private TeamMemberDTO team;
    private PublicVariables.UserRole role;

    public InvitationMemberDTO(Invitation invitation, TeamUserRoleService teamUserRoleService){
        this.UUID = invitation.getUUID();
        this.team = new TeamMemberDTO(invitation.getTeam(),null,teamUserRoleService);
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
