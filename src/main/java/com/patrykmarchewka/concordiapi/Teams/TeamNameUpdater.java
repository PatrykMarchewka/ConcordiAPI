package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;

public class TeamNameUpdater implements TeamCREATEUpdater,TeamPUTUpdater,TeamPATCHUpdater{
    @Override
    public void CREATEUpdate(Team team, TeamRequestBody body) {
        sharedUpdate(team,body);
    }

    @Override
    public void PATCHUpdate(Team team, TeamRequestBody body) {
        if (body.getName() != null){
            sharedUpdate(team, body);
        }
    }

    @Override
    public void PUTUpdate(Team team, TeamRequestBody body) {
        sharedUpdate(team, body);
    }

    void sharedUpdate(Team team, TeamRequestBody body){
        team.setName(body.getName());
    }
}
