package com.patrykmarchewka.concordiapi.Invitations.Updaters;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;

public interface InvitationCREATEUpdater extends InvitationUpdater{
    void CREATEUpdate(Invitation invitation, InvitationRequestBody body);
}
