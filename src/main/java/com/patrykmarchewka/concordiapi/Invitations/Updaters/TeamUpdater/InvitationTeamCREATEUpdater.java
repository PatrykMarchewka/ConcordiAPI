package com.patrykmarchewka.concordiapi.Invitations.Updaters.TeamUpdater;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationCREATEUpdater;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;

@Component
public class InvitationTeamCREATEUpdater implements InvitationCREATEUpdater {

    private final TeamService teamService;

    @Autowired
    public InvitationTeamCREATEUpdater(@Lazy TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    public void CREATEUpdate(Invitation invitation, InvitationRequestBody body) {
        invitation.setTeam(teamService.getTeamByID(body.getTeamID()));
    }
}
