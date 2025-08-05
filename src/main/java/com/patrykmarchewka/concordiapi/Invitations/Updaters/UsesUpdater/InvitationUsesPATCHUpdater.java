package com.patrykmarchewka.concordiapi.Invitations.Updaters.UsesUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvitationUsesPATCHUpdater implements InvitationPATCHUpdater {

    private final InvitationUsesUpdaterHelper invitationUsesUpdaterHelper;

    @Autowired
    public InvitationUsesPATCHUpdater(InvitationUsesUpdaterHelper invitationUsesUpdaterHelper) {
        this.invitationUsesUpdaterHelper = invitationUsesUpdaterHelper;
    }

    @Override
    public void PATCHUpdate(Invitation invitation, InvitationRequestBody body) {
        if (body.getUses() != null){
            invitationUsesUpdaterHelper.sharedUpdate(invitation, body);
        }
    }
}
