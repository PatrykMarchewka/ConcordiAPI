package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubtaskUpdatersPUT {
    private final List<SubtaskPUTUpdater> updaters;
    private final List<SubtaskPUTUpdaterWithTeam> updaterWithTeams;

    @Autowired
    public SubtaskUpdatersPUT(List<SubtaskPUTUpdater> updaters, List<SubtaskPUTUpdaterWithTeam> updaterWithTeams) {
        this.updaters = updaters;
        this.updaterWithTeams = updaterWithTeams;
    }

    /**
     * Applies PUT updates for the Subtask given the SubtaskRequestBody details, should only be called from {@link com.patrykmarchewka.concordiapi.Subtasks.SubtaskService#putUpdate(Subtask, SubtaskRequestBody, Long)}
     * @param subtask Subtask to edit
     * @param body SubtaskRequestBody
     * @param team Team containing subtask
     */
    void applyPutUpdates(Subtask subtask, SubtaskRequestBody body, Team team){
        for (SubtaskPUTUpdaterWithTeam updaterWithTeam : updaterWithTeams){
            updaterWithTeam.setTeam(team);
            updaterWithTeam.PUTUpdate(subtask, body);
        }

        for (SubtaskPUTUpdater updater : updaters){
            updater.PUTUpdate(subtask, body);
        }
    }
}
