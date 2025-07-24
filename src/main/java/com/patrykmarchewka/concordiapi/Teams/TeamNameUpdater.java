package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;



/**
 * Handles updating the {@code name} field of {@link Team} entities
 */
public class TeamNameUpdater implements TeamCREATEUpdater,TeamPUTUpdater,TeamPATCHUpdater{

    /**
     * Sets the team name during team creation
     * @param team Team being created
     * @param body TeamRequestBody with data containing team name
     */
    @Override
    public void CREATEUpdate(Team team, TeamRequestBody body) {
        sharedUpdate(team,body);
    }

    /**
     * Sets the team name if it's new value is provided in body
     * @param team Team to edit
     * @param body TeamRequestBody with data
     */
    @Override
    public void PATCHUpdate(Team team, TeamRequestBody body) {
        if (body.getName() != null){
            sharedUpdate(team, body);
        }
    }

    /**
     * Sets the team name to new value
     * @param team Team to edit
     * @param body TeamRequestBody with data containing team name
     */
    @Override
    public void PUTUpdate(Team team, TeamRequestBody body) {
        sharedUpdate(team, body);
    }

    /**
     * Shared logic for updating team's name
     * @param team Team to edit
     * @param body TeamRequestBody with data containing team name
     */
    void sharedUpdate(Team team, TeamRequestBody body){
        team.setName(body.getName());
    }
}
