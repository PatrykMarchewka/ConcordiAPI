package com.patrykmarchewka.concordiapi.Teams.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamUpdatersCREATE {

    private final List<TeamCREATEUpdater> updaters;

    @Autowired
    public TeamUpdatersCREATE(List<TeamCREATEUpdater> updaters) {
        this.updaters = updaters;
    }

    /**
     * Applies CREATE updates for the Team given the TeamRequestBody details, should be only called from {@link TeamUpdatersService#createUpdate(Team, TeamRequestBody)}
     * @param team Team to modify
     * @param body TeamRequestBody with information to update
     */
    void applyCreateUpdates(Team team, TeamRequestBody body){
        for (TeamCREATEUpdater updater : updaters){
            updater.CREATEUpdate(team, body);
        }
    }
}
