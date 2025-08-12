package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class SubtaskUpdatersCREATE {
    private final List<SubtaskCREATEUpdater> updaters;
    private final List<SubtaskCREATEUpdaterWithTeam> updatersWithTeam;

    @Autowired
    public SubtaskUpdatersCREATE(List<SubtaskCREATEUpdater> updaters, List<SubtaskCREATEUpdaterWithTeam> updatersWithTeam) {
        this.updaters = updaters;
        this.updatersWithTeam = updatersWithTeam;
    }

    /**
     * Applies CREATE updates for the Subtask given the SubtaskRequestBody details, should only be called from {@link SubtaskUpdatersService#update(Subtask, SubtaskRequestBody, Supplier, UpdateType)}
     * @param subtask Subtask to create
     * @param body SubtaskRequestBody with new values
     * @param team Team containing the subtask
     */
    void applyCreateUpdates(Subtask subtask, SubtaskRequestBody body, Supplier<Team> team){
        for (SubtaskCREATEUpdaterWithTeam updaterWithTeam : updatersWithTeam){
            updaterWithTeam.setTeam(team.get());
            updaterWithTeam.CREATEUpdate(subtask, body);
        }

        for (SubtaskCREATEUpdater updater : updaters){
            updater.CREATEUpdate(subtask,body);
        }
    }
}
