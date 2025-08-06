package com.patrykmarchewka.concordiapi.Invitations.Updaters.UsesUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvitationUsesPUTUpdater implements InvitationPUTUpdater {

    private final InvitationUsesUpdaterHelper invitationUsesUpdaterHelper;

    @Autowired
    public InvitationUsesPUTUpdater(InvitationUsesUpdaterHelper invitationUsesUpdaterHelper) {
        this.invitationUsesUpdaterHelper = invitationUsesUpdaterHelper;
    }

    @Override
    public void PUTUpdate(Invitation invitation, InvitationRequestBody body) {
        invitationUsesUpdaterHelper.sharedUpdate(invitation, body);
    }
}
