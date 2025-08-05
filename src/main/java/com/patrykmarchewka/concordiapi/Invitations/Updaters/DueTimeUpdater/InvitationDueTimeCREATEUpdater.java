package com.patrykmarchewka.concordiapi.Invitations.Updaters.DueTimeUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationCREATEUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvitationDueTimeCREATEUpdater implements InvitationCREATEUpdater {

    private final InvitationDueTimeUpdaterHelper invitationDueTimeUpdaterHelper;

    @Autowired
    public InvitationDueTimeCREATEUpdater(InvitationDueTimeUpdaterHelper invitationDueTimeUpdaterHelper) {
        this.invitationDueTimeUpdaterHelper = invitationDueTimeUpdaterHelper;
    }

    @Override
    public void CREATEUpdate(Invitation invitation, InvitationRequestBody body) {
        invitationDueTimeUpdaterHelper.sharedUpdate(invitation, body);
    }
}
