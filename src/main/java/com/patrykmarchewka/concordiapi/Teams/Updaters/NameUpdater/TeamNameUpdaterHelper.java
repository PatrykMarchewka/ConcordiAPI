package com.patrykmarchewka.concordiapi.Teams.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import org.springframework.stereotype.Service;

@Service
public class TeamNameUpdaterHelper {
    /**
     * Shared logic for updating team's name
     * @param team Team to edit
     * @param body TeamRequestBody with data containing team name
     */
    void sharedUpdate(Team team, TeamRequestBody body){
        team.setName(body.getName());
    }
}
