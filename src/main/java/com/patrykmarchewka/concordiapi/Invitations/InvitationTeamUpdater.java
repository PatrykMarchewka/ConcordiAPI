package com.patrykmarchewka.concordiapi.Invitations;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.Teams.TeamService;

public class InvitationTeamUpdater implements InvitationCREATEUpdater{

    private final TeamService teamService;

    public InvitationTeamUpdater(TeamService teamService){
        this.teamService = teamService;
    }

    @Override
    public void CREATEUpdate(Invitation invitation, InvitationRequestBody body) {
        invitation.setTeam(teamService.getTeamByID(body.getTeamID()));
    }
}
