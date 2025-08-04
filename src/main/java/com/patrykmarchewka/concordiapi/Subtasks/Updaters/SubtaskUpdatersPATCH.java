package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * Applies PATCH updates for the Subtask given the SubtaskRequestBody details, should only be called from {@link com.patrykmarchewka.concordiapi.Subtasks.SubtaskService#patchUpdate(Subtask, SubtaskRequestBody, Long)}
     * @param subtask Subtask to edit
     * @param body SubtaskRequestBody with new values
     * @param team Team containing the subtask
     */
    void applyPatchUpdates(Subtask subtask, SubtaskRequestBody body, Team team){
        for (SubtaskPATCHUpdaterWithTeam updaterWithTeam : updatersWithTeamList){
            updaterWithTeam.setTeam(team);
            updaterWithTeam.PATCHUpdate(subtask, body);
        }

        for (SubtaskPATCHUpdater updater : updaters){
            updater.PATCHUpdate(subtask, body);
        }
    }
}
