package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class SubtaskUpdatersPATCH {

    private final List<SubtaskPATCHUpdater> updaters;

    @Autowired
    public SubtaskUpdatersPATCH(List<SubtaskPATCHUpdater> updaters) {
        this.updaters = updaters;
    }

    /**
     * Applies PATCH updates for the Subtask given the SubtaskRequestBody details, should only be called from {@link SubtaskUpdatersService#update(Subtask, SubtaskRequestBody, Supplier, UpdateType)}
     * @param subtask Subtask to edit
     * @param body SubtaskRequestBody with new values
     */
    void applyPatchUpdates(Subtask subtask, SubtaskRequestBody body){
        for (SubtaskPATCHUpdater updater : updaters){
            updater.PATCHUpdate(subtask, body);
        }
    }
}
