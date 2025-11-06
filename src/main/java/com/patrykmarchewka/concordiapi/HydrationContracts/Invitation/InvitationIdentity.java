package com.patrykmarchewka.concordiapi.HydrationContracts.Invitation;

import com.patrykmarchewka.concordiapi.UserRole;

import java.time.OffsetDateTime;

public interface InvitationIdentity {
    String getUUID();
    short getUses();
    UserRole getRole();
    OffsetDateTime getDueTime();
}
