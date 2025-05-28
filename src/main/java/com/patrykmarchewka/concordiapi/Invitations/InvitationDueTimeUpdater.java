package com.patrykmarchewka.concordiapi.Invitations;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;

public class InvitationDueTimeUpdater implements InvitationCREATEUpdater,InvitationPUTUpdater,InvitationPATCHUpdater{
    @Override
    public void CREATEUpdate(Invitation invitation, InvitationRequestBody body) {
        sharedUpdate(invitation, body);
    }

    @Override
    public void PATCHUpdate(Invitation invitation, InvitationRequestBody body) {
        if (body.getDueDate() != null){
            sharedUpdate(invitation, body);
        }
    }

    @Override
    public void PUTUpdate(Invitation invitation, InvitationRequestBody body) {
        sharedUpdate(invitation, body);
    }

    void sharedUpdate(Invitation invitation, InvitationRequestBody body){
        invitation.setDueTime(body.getDueDate());
    }
}
