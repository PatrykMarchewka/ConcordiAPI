package com.patrykmarchewka.concordiapi.Invitations.Updaters.DueTimeUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvitationDueTimePUTUpdater implements InvitationPUTUpdater {

    private final InvitationDueTimeUpdaterHelper invitationDueTimeUpdaterHelper;

    @Autowired
    public InvitationDueTimePUTUpdater(InvitationDueTimeUpdaterHelper invitationDueTimeUpdaterHelper) {
        this.invitationDueTimeUpdaterHelper = invitationDueTimeUpdaterHelper;
    }

    @Override
    public void PUTUpdate(Invitation invitation, InvitationRequestBody body) {
        invitationDueTimeUpdaterHelper.sharedUpdate(invitation, body);
    }
}
