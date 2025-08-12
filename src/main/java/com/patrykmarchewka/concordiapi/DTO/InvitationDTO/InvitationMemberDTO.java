package com.patrykmarchewka.concordiapi.DTO.InvitationDTO;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.UserRole;

public class InvitationMemberDTO {
    private String UUID;
    private TeamMemberDTO team;
    private UserRole role;

    public InvitationMemberDTO(Invitation invitation, TeamUserRoleService teamUserRoleService){
        this.UUID = invitation.getUUID();
        this.team = new TeamMemberDTO(invitation.getInvitingTeam(),null,teamUserRoleService);
        this.role = invitation.getRole();
    }

    public InvitationMemberDTO(){}

    public String getUUID(){return this.UUID;}
    public void setUUID(String UUID){this.UUID = UUID;}

    public TeamMemberDTO getTeam(){return this.team;}
    public void setTeam(TeamMemberDTO team){this.team = team;}

    public UserRole getRole(){return this.role;}
    public void setRole(UserRole role){this.role = role;}
}
