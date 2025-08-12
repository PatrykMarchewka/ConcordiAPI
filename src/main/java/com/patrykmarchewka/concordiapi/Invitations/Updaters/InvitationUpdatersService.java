package com.patrykmarchewka.concordiapi.Invitations.Updaters;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.UpdateType;
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

    public void update(Invitation invitation, InvitationRequestBody body, UpdateType type, Supplier<Team> teamSupplier){
        switch (type){
            case CREATE -> invitationUpdatersCREATE.applyCreateUpdates(invitation, body, teamSupplier);
            case PUT -> invitationUpdatersPUT.applyPutUpdates(invitation, body);
            case PATCH -> invitationUpdatersPATCH.applyPatchUpdates(invitation, body);
            case null, default -> throw new BadRequestException("Called update type that isn't CREATE/PUT/PATCH");
        }
    }
}
