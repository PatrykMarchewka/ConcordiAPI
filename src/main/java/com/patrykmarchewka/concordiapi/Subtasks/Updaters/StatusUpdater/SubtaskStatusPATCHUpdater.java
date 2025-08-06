package com.patrykmarchewka.concordiapi.Subtasks.Updaters.StatusUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubtaskStatusPATCHUpdater implements SubtaskPATCHUpdater {

    private final SubtaskStatusUpdaterHelper subtaskStatusUpdaterHelper;

    @Autowired
    public SubtaskStatusPATCHUpdater(SubtaskStatusUpdaterHelper subtaskStatusUpdaterHelper) {
        this.subtaskStatusUpdaterHelper = subtaskStatusUpdaterHelper;
    }


    @Override
    public void PATCHUpdate(Subtask subtask, SubtaskRequestBody body) {
        subtaskStatusUpdaterHelper.sharedUpdate(subtask, body);
    }
}
