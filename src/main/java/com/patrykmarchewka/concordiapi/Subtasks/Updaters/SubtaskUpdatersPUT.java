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
public class SubtaskUpdatersPUT {
    private final List<SubtaskPUTUpdater> updaters;
    private final List<SubtaskPUTUpdaterWithTeam> updaterWithTeams;

    @Autowired
    public SubtaskUpdatersPUT(List<SubtaskPUTUpdater> updaters, List<SubtaskPUTUpdaterWithTeam> updaterWithTeams) {
        this.updaters = updaters;
        this.updaterWithTeams = updaterWithTeams;
    }

    /**
     * Applies PUT updates for the Subtask given the SubtaskRequestBody details, should only be called from {@link SubtaskUpdatersService#update(Subtask, SubtaskRequestBody, Supplier, UpdateType)}
     * @param subtask Subtask to edit
     * @param body SubtaskRequestBody
     * @param team Team containing subtask
     */
    void applyPutUpdates(Subtask subtask, SubtaskRequestBody body, Supplier<Team> team){
        for (SubtaskPUTUpdaterWithTeam updaterWithTeam : updaterWithTeams){
            updaterWithTeam.setTeam(team.get());
            updaterWithTeam.PUTUpdate(subtask, body);
        }

        for (SubtaskPUTUpdater updater : updaters){
            updater.PUTUpdate(subtask, body);
        }
    }
}
