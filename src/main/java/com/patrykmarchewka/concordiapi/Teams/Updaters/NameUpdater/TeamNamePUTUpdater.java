package com.patrykmarchewka.concordiapi.Teams.Updaters.NameUpdater;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Teams.Updaters.TeamPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TeamNamePUTUpdater implements TeamPUTUpdater {

    private final TeamNameUpdaterHelper teamNameUpdaterHelper;

    @Autowired
    public TeamNamePUTUpdater(TeamNameUpdaterHelper teamNameUpdaterHelper) {
        this.teamNameUpdaterHelper = teamNameUpdaterHelper;
    }

    /**
     * Sets the team name to new value
     * @param team Team to edit
     * @param body TeamRequestBody with data containing team name
     */
    @Override
    public void PUTUpdate(Team team, TeamRequestBody body) {
        teamNameUpdaterHelper.sharedUpdate(team, body);
    }
}
