package com.patrykmarchewka.concordiapi.Invitations.Updaters.RoleUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import org.springframework.stereotype.Service;

@Service
public class InvitationRoleUpdaterHelper {
    void sharedUpdate(Invitation invitation, InvitationRequestBody body){
        invitation.setRole(body.getRole());
    }
}
