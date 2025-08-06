package com.patrykmarchewka.concordiapi.Invitations.Updaters.RoleUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationCREATEUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvitationRoleCREATEUpdater implements InvitationCREATEUpdater {

    private final InvitationRoleUpdaterHelper invitationRoleUpdaterHelper;

    @Autowired
    public InvitationRoleCREATEUpdater(InvitationRoleUpdaterHelper invitationRoleUpdaterHelper) {
        this.invitationRoleUpdaterHelper = invitationRoleUpdaterHelper;
    }

    @Override
    public void CREATEUpdate(Invitation invitation, InvitationRequestBody body) {
        invitationRoleUpdaterHelper.sharedUpdate(invitation, body);
    }
}
