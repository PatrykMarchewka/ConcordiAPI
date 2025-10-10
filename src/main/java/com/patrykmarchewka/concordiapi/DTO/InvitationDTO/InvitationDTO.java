package com.patrykmarchewka.concordiapi.DTO.InvitationDTO;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.UserRole;

import java.util.Objects;

public interface InvitationDTO {
    String getUUID();
    TeamMemberDTO getTeam();
    UserRole getRole();


    default boolean equalsInvitation(Invitation invitation){
        return Objects.equals(getUUID(), invitation.getUUID()) &&
                Objects.equals(getTeam().getID(), invitation.getInvitingTeam().getID()) &&
                Objects.equals(getRole(), invitation.getRole());
    }
}
