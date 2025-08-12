package com.patrykmarchewka.concordiapi.Invitations.Updaters.TeamUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationCREATEUpdaterBasicWithTeam;
import org.springframework.stereotype.Component;

@Component
public class InvitationTeamCREATEUpdater implements InvitationCREATEUpdaterBasicWithTeam {

    private Team team;

    @Override
    public void CREATEUpdate(Invitation invitation) {
        if (this.team == null){
            throw new BadRequestException("The team is set to null");
        }
        invitation.setInvitingTeam(team);
    }

    @Override
    public void setTeam(Team team) {
        this.team = team;
    }


}
