package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubtaskUpdatersPUT {
    private final List<SubtaskPUTUpdater> updaters;

    @Autowired
    public SubtaskUpdatersPUT(List<SubtaskPUTUpdater> updaters) {
        this.updaters = updaters;
    }

    /**
     * Applies PUT updates for the Subtask given the SubtaskRequestBody details, should only be called from {@link SubtaskUpdatersService#putUpdate(Subtask, SubtaskRequestBody)}
     * @param subtask Subtask to edit
     * @param body SubtaskRequestBody
     */
    void applyPutUpdates(Subtask subtask, SubtaskRequestBody body){
        for (SubtaskPUTUpdater updater : updaters){
            updater.PUTUpdate(subtask, body);
        }
    }
}
