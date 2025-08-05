package com.patrykmarchewka.concordiapi.Teams.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Teams.Updaters.TeamPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TeamNamePATCHUpdater implements TeamPATCHUpdater {

    private final TeamNameUpdaterHelper teamNameUpdaterHelper;

    @Autowired
    public TeamNamePATCHUpdater(TeamNameUpdaterHelper teamNameUpdaterHelper) {
        this.teamNameUpdaterHelper = teamNameUpdaterHelper;
    }

    /**
     * Sets the team name if its new value is provided in body
     * @param team Team to edit
     * @param body TeamRequestBody with data
     */
    @Override
    public void PATCHUpdate(Team team, TeamRequestBody body) {
        if (body.getName() != null){
            teamNameUpdaterHelper.sharedUpdate(team, body);
        }
    }
}
