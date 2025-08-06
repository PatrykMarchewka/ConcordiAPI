package com.patrykmarchewka.concordiapi.Invitations.Updaters;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvitationUpdatersPUT {

    private final List<InvitationPUTUpdater> updaters;

    @Autowired
    public InvitationUpdatersPUT(List<InvitationPUTUpdater> updaters) {
        this.updaters = updaters;
    }

    /**
     * Applies PUT updates for the Invitation given the InvitationRequestBody, should only be called from {@link com.patrykmarchewka.concordiapi.Invitations.InvitationService#putUpdate(Invitation, InvitationRequestBody)}
     * @param invitation Invitation to edit
     * @param body InvitationRequestBody with information to update
     */
    void applyPutUpdates(Invitation invitation, InvitationRequestBody body){
        for (InvitationPUTUpdater updater : updaters){
            updater.PUTUpdate(invitation, body);
        }
    }
}
