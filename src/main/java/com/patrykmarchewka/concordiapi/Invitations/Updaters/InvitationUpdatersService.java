package com.patrykmarchewka.concordiapi.Invitations.Updaters;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class InvitationUpdatersService {

    private final InvitationUpdatersCREATE invitationUpdatersCREATE;
    private final InvitationUpdatersPUT invitationUpdatersPUT;
    private final InvitationUpdatersPATCH invitationUpdatersPATCH;

    @Autowired
    public InvitationUpdatersService(InvitationUpdatersCREATE invitationUpdatersCREATE, InvitationUpdatersPUT invitationUpdatersPUT, InvitationUpdatersPATCH invitationUpdatersPATCH) {
        this.invitationUpdatersCREATE = invitationUpdatersCREATE;
        this.invitationUpdatersPUT = invitationUpdatersPUT;
        this.invitationUpdatersPATCH = invitationUpdatersPATCH;
    }

    public void createUpdate(Invitation invitation, InvitationRequestBody body, Supplier<Team> teamSupplier){
        invitationUpdatersCREATE.applyCreateUpdates(invitation, body, teamSupplier);
    }

    public void putUpdate(Invitation invitation, InvitationRequestBody body){
        invitationUpdatersPUT.applyPutUpdates(invitation, body);
    }

    public void patchUpdate(Invitation invitation, InvitationRequestBody body){
        invitationUpdatersPATCH.applyPatchUpdates(invitation, body);
    }
}
