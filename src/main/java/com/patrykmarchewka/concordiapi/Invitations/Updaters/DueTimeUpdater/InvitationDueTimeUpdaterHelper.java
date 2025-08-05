package com.patrykmarchewka.concordiapi.Invitations.Updaters.DueTimeUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import org.springframework.stereotype.Service;

@Service
public class InvitationDueTimeUpdaterHelper {
    void sharedUpdate(Invitation invitation, InvitationRequestBody body){
        invitation.setDueTime(body.getDueDate());
    }
}
