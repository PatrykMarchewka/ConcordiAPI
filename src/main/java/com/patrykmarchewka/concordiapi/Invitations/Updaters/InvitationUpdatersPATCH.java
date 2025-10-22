package com.patrykmarchewka.concordiapi.Invitations.Updaters;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class InvitationUpdatersPATCH {

    private final List<InvitationPATCHUpdater> updaters;

    @Autowired
    public InvitationUpdatersPATCH(List<InvitationPATCHUpdater> updaters) {
        this.updaters = updaters;
    }

    /**
     * Applies PATCH updates for the Invitation given the InvitationRequestBody, should only be called from {@link InvitationUpdatersService#patchUpdate(Invitation, InvitationRequestBody)}
     * @param invitation Invitation to edit
     * @param body InvitationRequestBody with information to update
     */
    void applyPatchUpdates(Invitation invitation, InvitationRequestBody body){
        for (InvitationPATCHUpdater updater : updaters){
            updater.PATCHUpdate(invitation, body);
        }
    }
}
