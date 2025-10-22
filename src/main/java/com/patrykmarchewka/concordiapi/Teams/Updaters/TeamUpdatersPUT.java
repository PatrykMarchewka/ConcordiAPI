package com.patrykmarchewka.concordiapi.Teams.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamUpdatersPUT {

    private final List<TeamPUTUpdater> updaters;

    @Autowired
    public TeamUpdatersPUT(List<TeamPUTUpdater> updaters) {
        this.updaters = updaters;
    }


    /**
     * Applies PUT updates for the Team given the TeamRequestBody details, should be only called from {@link TeamUpdatersService#putUpdate(Team, TeamRequestBody)}
     * @param team Team to modify
     * @param body TeamRequestBody with information to update
     */
    void applyPutUpdates(Team team, TeamRequestBody body){
        for (TeamPUTUpdater updater : updaters){
            updater.PUTUpdate(team, body);
        }
    }
}
