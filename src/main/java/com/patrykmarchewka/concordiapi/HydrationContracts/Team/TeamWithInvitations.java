package com.patrykmarchewka.concordiapi.HydrationContracts.Team;

import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;

import java.util.Set;

public interface TeamWithInvitations extends TeamIdentity{
    Set<Invitation> getInvitations();
}
