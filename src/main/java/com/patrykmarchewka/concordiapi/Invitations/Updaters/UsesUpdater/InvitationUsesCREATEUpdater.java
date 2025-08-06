package com.patrykmarchewka.concordiapi.Invitations.Updaters.UsesUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationCREATEUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvitationUsesCREATEUpdater implements InvitationCREATEUpdater {

    private final InvitationUsesUpdaterHelper invitationUsesUpdaterHelper;

    @Autowired
    public InvitationUsesCREATEUpdater(InvitationUsesUpdaterHelper invitationUsesUpdaterHelper) {
        this.invitationUsesUpdaterHelper = invitationUsesUpdaterHelper;
    }

    @Override
    public void CREATEUpdate(Invitation invitation, InvitationRequestBody body) {
        invitationUsesUpdaterHelper.sharedUpdate(invitation, body);
    }
}
