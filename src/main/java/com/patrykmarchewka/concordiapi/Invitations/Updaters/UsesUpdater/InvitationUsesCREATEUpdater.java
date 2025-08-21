package com.patrykmarchewka.concordiapi.Invitations.Updaters.UsesUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationCREATEUpdater;
import org.springframework.stereotype.Component;

@Component
public class InvitationUsesCREATEUpdater implements InvitationCREATEUpdater {
    @Override
    public void CREATEUpdate(Invitation invitation, InvitationRequestBody body) {
        invitation.setUses((body.getUses()) != null ? body.getUses() : 1);
    }
}
