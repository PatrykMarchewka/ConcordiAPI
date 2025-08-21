package com.patrykmarchewka.concordiapi.Invitations.Updaters.RoleUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationCREATEUpdater;
import com.patrykmarchewka.concordiapi.UserRole;
import org.springframework.stereotype.Component;

@Component
public class InvitationRoleCREATEUpdater implements InvitationCREATEUpdater {
    @Override
    public void CREATEUpdate(Invitation invitation, InvitationRequestBody body) {
        invitation.setRole((body.getRole() != null) ? body.getRole() : UserRole.MEMBER);
    }
}
