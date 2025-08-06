package com.patrykmarchewka.concordiapi.Subtasks.Updaters.StatusUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubtaskStatusPUTUpdater implements SubtaskPUTUpdater {


    private final SubtaskStatusUpdaterHelper subtaskStatusUpdaterHelper;

    @Autowired
    public SubtaskStatusPUTUpdater(SubtaskStatusUpdaterHelper subtaskStatusUpdaterHelper) {
        this.subtaskStatusUpdaterHelper = subtaskStatusUpdaterHelper;
    }

    @Override
    public void PUTUpdate(Subtask subtask, SubtaskRequestBody body) {
        subtaskStatusUpdaterHelper.sharedUpdate(subtask, body);
    }
}
