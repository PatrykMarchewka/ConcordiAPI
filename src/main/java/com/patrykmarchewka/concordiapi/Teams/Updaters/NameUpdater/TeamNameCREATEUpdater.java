package com.patrykmarchewka.concordiapi.Teams.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Teams.Updaters.TeamCREATEUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TeamNameCREATEUpdater implements TeamCREATEUpdater {

    private final TeamNameUpdaterHelper teamNameUpdaterHelper;

    @Autowired
    public TeamNameCREATEUpdater(TeamNameUpdaterHelper teamNameUpdaterHelper) {
        this.teamNameUpdaterHelper = teamNameUpdaterHelper;
    }

    /**
     * Sets the team name during team creation
     * @param team Team being created
     * @param body TeamRequestBody with data containing team name
     */
    @Override
    public void CREATEUpdate(Team team, TeamRequestBody body) {
        teamNameUpdaterHelper.sharedUpdate(team, body);
    }
}
