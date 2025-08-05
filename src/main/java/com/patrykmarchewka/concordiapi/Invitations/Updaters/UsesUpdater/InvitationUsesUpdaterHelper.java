package com.patrykmarchewka.concordiapi.Invitations.Updaters.UsesUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import org.springframework.stereotype.Service;

@Service
public class InvitationUsesUpdaterHelper {
    void sharedUpdate(Invitation invitation, InvitationRequestBody body){
        invitation.setUses(body.getUses());
    }
}
