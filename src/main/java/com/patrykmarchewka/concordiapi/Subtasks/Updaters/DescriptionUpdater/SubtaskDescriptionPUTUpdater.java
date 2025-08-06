package com.patrykmarchewka.concordiapi.Subtasks.Updaters.DescriptionUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskPUTUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubtaskDescriptionPUTUpdater implements SubtaskPUTUpdater {

    private final SubtaskDescriptionUpdaterHelper subtaskDescriptionUpdaterHelper;

    @Autowired
    public SubtaskDescriptionPUTUpdater(SubtaskDescriptionUpdaterHelper subtaskDescriptionUpdaterHelper) {
        this.subtaskDescriptionUpdaterHelper = subtaskDescriptionUpdaterHelper;
    }

    @Override
    public void PUTUpdate(Subtask subtask, SubtaskRequestBody body) {
        subtaskDescriptionUpdaterHelper.sharedUpdate(subtask, body);
    }
}
