package com.patrykmarchewka.concordiapi.HydrationContracts.Invitation;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;

public interface InvitationWithTeam extends InvitationIdentity{
    Team getInvitingTeam();
}
