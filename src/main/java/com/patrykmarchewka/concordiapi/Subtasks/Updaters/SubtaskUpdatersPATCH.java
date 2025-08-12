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
public class SubtaskUpdatersPATCH {

    private final List<SubtaskPATCHUpdater> updaters;
    private final List<SubtaskPATCHUpdaterWithTeam> updatersWithTeamList;

    @Autowired
    public SubtaskUpdatersPATCH(List<SubtaskPATCHUpdater> updaters, List<SubtaskPATCHUpdaterWithTeam> updatersWithTeamList) {
        this.updaters = updaters;
        this.updatersWithTeamList = updatersWithTeamList;
    }

    /**
     * Applies PATCH updates for the Subtask given the SubtaskRequestBody details, should only be called from {@link SubtaskUpdatersService#update(Subtask, SubtaskRequestBody, Supplier, UpdateType)}
     * @param subtask Subtask to edit
     * @param body SubtaskRequestBody with new values
     * @param team Team containing the subtask
     */
    void applyPatchUpdates(Subtask subtask, SubtaskRequestBody body, Supplier<Team> team){
        for (SubtaskPATCHUpdaterWithTeam updaterWithTeam : updatersWithTeamList){
            updaterWithTeam.setTeam(team.get());
            updaterWithTeam.PATCHUpdate(subtask, body);
        }
        for (SubtaskPATCHUpdater updater : updaters){
            updater.PATCHUpdate(subtask, body);
        }
    }
}
