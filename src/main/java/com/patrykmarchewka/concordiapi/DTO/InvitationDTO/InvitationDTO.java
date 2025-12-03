package com.patrykmarchewka.concordiapi.DTO.InvitationDTO;

import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationIdentity;
import com.patrykmarchewka.concordiapi.UserRole;

import java.time.OffsetDateTime;
import java.util.Objects;

public interface InvitationDTO extends InvitationIdentity {
    void setUUID(String UUID);
    void setUses(short uses);
    void setRole(UserRole role);
    void setDueTime(OffsetDateTime dueTime);

    default boolean equalsInvitation(InvitationIdentity invitation){
        return Objects.equals(getUUID(), invitation.getUUID()) &&
                getUses() == invitation.getUses() &&
                getRole() == invitation.getRole() &&
                Objects.equals(getDueTime(), invitation.getDueTime());
    }
}
