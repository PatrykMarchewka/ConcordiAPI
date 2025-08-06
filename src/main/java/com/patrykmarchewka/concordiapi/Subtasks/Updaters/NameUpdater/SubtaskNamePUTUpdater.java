package com.patrykmarchewka.concordiapi.Subtasks.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubtaskNamePUTUpdater implements SubtaskPUTUpdater {

    private final SubtaskNameUpdaterHelper subtaskNameUpdaterHelper;

    @Autowired
    public SubtaskNamePUTUpdater(SubtaskNameUpdaterHelper subtaskNameUpdaterHelper) {
        this.subtaskNameUpdaterHelper = subtaskNameUpdaterHelper;
    }


    @Override
    public void PUTUpdate(Subtask subtask, SubtaskRequestBody body) {
        subtaskNameUpdaterHelper.sharedUpdate(subtask, body);
    }
}
