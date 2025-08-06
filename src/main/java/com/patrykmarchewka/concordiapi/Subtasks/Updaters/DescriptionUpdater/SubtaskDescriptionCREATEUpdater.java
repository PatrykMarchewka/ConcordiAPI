package com.patrykmarchewka.concordiapi.Subtasks.Updaters.DescriptionUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskCREATEUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubtaskDescriptionCREATEUpdater implements SubtaskCREATEUpdater {

    private final SubtaskDescriptionUpdaterHelper subtaskDescriptionUpdaterHelper;

    @Autowired
    public SubtaskDescriptionCREATEUpdater(SubtaskDescriptionUpdaterHelper subtaskDescriptionUpdaterHelper) {
        this.subtaskDescriptionUpdaterHelper = subtaskDescriptionUpdaterHelper;
    }


    @Override
    public void CREATEUpdate(Subtask subtask, SubtaskRequestBody body) {
        subtaskDescriptionUpdaterHelper.sharedUpdate(subtask, body);
    }
}
