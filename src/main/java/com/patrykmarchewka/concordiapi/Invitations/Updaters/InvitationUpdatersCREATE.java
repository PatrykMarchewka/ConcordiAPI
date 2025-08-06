package com.patrykmarchewka.concordiapi.Invitations.Updaters;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvitationUpdatersCREATE {

    private final List<InvitationCREATEUpdater> updaters;

    @Autowired
    public InvitationUpdatersCREATE(List<InvitationCREATEUpdater> updaters) {
        this.updaters = updaters;
    }

    /**
     * Applies CREATE updates for the Invitation given the InvitationRequestBody, should only be called from {@link com.patrykmarchewka.concordiapi.Invitations.InvitationService#createInvitation(InvitationRequestBody)}
     * @param invitation Invitation to create
     * @param body InvitationRequestBody with information to update
     */
    void applyCreateUpdates(Invitation invitation, InvitationRequestBody body){
        for (InvitationCREATEUpdater updater : updaters){
            updater.CREATEUpdate(invitation, body);
        }
    }
}
