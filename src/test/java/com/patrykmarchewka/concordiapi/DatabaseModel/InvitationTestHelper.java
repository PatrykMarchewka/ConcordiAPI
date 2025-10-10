package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.UserRole;

public interface InvitationTestHelper {
    default Invitation createInvitation(Team team, UserRole userRole, int plusOffset, InvitationRepository invitationRepository){
        Invitation invitation = new Invitation();
        invitation.setInvitingTeam(team);
        invitation.setRole(userRole);
        invitation.setUses((short) 101);
        invitation.setDueTime(OffsetDateTimeConverter.nowConverted().plusDays(plusOffset));
        return invitationRepository.save(invitation);
    }
}
