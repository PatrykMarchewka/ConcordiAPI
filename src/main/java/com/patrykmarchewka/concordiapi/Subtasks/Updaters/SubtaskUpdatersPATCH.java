package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubtaskUpdatersPATCH {

    private final List<SubtaskPATCHUpdater> updaters;

    @Autowired
    public SubtaskUpdatersPATCH(List<SubtaskPATCHUpdater> updaters) {
        this.updaters = updaters;
    }

    /**
     * Applies PATCH updates for the Subtask given the SubtaskRequestBody details, should only be called from {@link SubtaskUpdatersService#patchUpdate(Subtask, SubtaskRequestBody)}
     * @param subtask Subtask to edit
     * @param body SubtaskRequestBody with new values
     */
    void applyPatchUpdates(Subtask subtask, SubtaskRequestBody body){
        for (SubtaskPATCHUpdater updater : updaters){
            updater.PATCHUpdate(subtask, body);
        }
    }
}
