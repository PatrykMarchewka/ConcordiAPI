package com.patrykmarchewka.concordiapi.Teams.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamUpdatersPATCH {

    private final List<TeamPATCHUpdater> updaters;

    @Autowired
    public TeamUpdatersPATCH(List<TeamPATCHUpdater> updaters) {
        this.updaters = updaters;
    }

    /**
     * Applies PATCH updates for the Team given the TeamRequestBody details, should be only called from {@link TeamUpdatersService#patchUpdate(Team, TeamRequestBody)}
     * @param team Team to modify
     * @param body TeamRequestBody with information to update
     */
    void applyPatchUpdates(Team team, TeamRequestBody body){
        for (TeamPATCHUpdater updater : updaters){
            updater.PATCHUpdate(team, body);
        }
    }
}
