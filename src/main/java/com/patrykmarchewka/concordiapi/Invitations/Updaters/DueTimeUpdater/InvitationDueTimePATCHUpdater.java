package com.patrykmarchewka.concordiapi.Invitations.Updaters.DueTimeUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvitationDueTimePATCHUpdater implements InvitationPATCHUpdater {

    private final InvitationDueTimeUpdaterHelper invitationDueTimeUpdaterHelper;

    @Autowired
    public InvitationDueTimePATCHUpdater(InvitationDueTimeUpdaterHelper invitationDueTimeUpdaterHelper) {
        this.invitationDueTimeUpdaterHelper = invitationDueTimeUpdaterHelper;
    }

    @Override
    public void PATCHUpdate(Invitation invitation, InvitationRequestBody body) {
        if (body.getDueDate() != null){
            invitationDueTimeUpdaterHelper.sharedUpdate(invitation, body);
        }
    }
}
