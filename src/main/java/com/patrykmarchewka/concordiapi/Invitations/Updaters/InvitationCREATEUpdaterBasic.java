package com.patrykmarchewka.concordiapi.Invitations.Updaters;

import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;

public interface InvitationCREATEUpdaterBasic extends InvitationUpdater{
    void CREATEUpdate(Invitation invitation);
}
