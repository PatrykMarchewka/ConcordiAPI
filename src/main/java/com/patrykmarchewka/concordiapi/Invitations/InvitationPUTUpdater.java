package com.patrykmarchewka.concordiapi.Invitations;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;

public interface InvitationPUTUpdater extends InvitationUpdater{
    void PUTUpdate(Invitation invitation, InvitationRequestBody body);
}
