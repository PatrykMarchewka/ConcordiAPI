package com.patrykmarchewka.concordiapi.DTO.InvitationDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationIdentity;

import java.util.Objects;

public interface InvitationDTO extends InvitationIdentity {

    default boolean equalsInvitation(Invitation invitation){
        return Objects.equals(getUUID(), invitation.getUUID());
    }
}
