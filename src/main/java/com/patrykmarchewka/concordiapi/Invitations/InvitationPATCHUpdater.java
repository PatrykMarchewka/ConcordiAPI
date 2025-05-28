package com.patrykmarchewka.concordiapi.Invitations;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;

public interface InvitationPATCHUpdater extends InvitationUpdater{
    void PATCHUpdate(Invitation invitation, InvitationRequestBody body);
}
