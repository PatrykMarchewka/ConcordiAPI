package com.patrykmarchewka.concordiapi.Invitations.Updaters.RoleUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvitationRolePUTUpdater implements InvitationPUTUpdater {

    private final InvitationRoleUpdaterHelper invitationRoleUpdaterHelper;

    @Autowired
    public InvitationRolePUTUpdater(InvitationRoleUpdaterHelper invitationRoleUpdaterHelper) {
        this.invitationRoleUpdaterHelper = invitationRoleUpdaterHelper;
    }

    @Override
    public void PUTUpdate(Invitation invitation, InvitationRequestBody body) {
        invitationRoleUpdaterHelper.sharedUpdate(invitation, body);
    }
}
