package com.patrykmarchewka.concordiapi.Invitations;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.UserRole;

import java.time.OffsetDateTime;

public interface InvitationRequestBodyHelper {
    default InvitationRequestBody createInvitationRequestBody(UserRole role){
        InvitationRequestBody body = new InvitationRequestBody();
        body.setRole(role);
        return body;
    }

    default InvitationRequestBody createInvitationRequestBody(UserRole role, Short uses, OffsetDateTime dueDate){
        InvitationRequestBody body = new InvitationRequestBody();
        body.setRole(role);
        body.setUses(uses);
        body.setDueDate(dueDate);
        return body;
    }
}
