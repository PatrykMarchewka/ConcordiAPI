package com.patrykmarchewka.concordiapi.Invitations.Updaters.RoleUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvitationRolePATCHUpdater implements InvitationPATCHUpdater {

    private final InvitationRoleUpdaterHelper invitationRoleUpdaterHelper;

    @Autowired
    public InvitationRolePATCHUpdater(InvitationRoleUpdaterHelper invitationRoleUpdaterHelper) {
        this.invitationRoleUpdaterHelper = invitationRoleUpdaterHelper;
    }

    @Override
    public void PATCHUpdate(Invitation invitation, InvitationRequestBody body) {
        if (body.getRole() != null){
            invitationRoleUpdaterHelper.sharedUpdate(invitation, body);
        }
    }
}
