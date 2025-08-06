package com.patrykmarchewka.concordiapi.Subtasks.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskCREATEUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubtaskNameCREATEUpdater implements SubtaskCREATEUpdater {

    private final SubtaskNameUpdaterHelper subtaskNameUpdaterHelper;

    @Autowired
    public SubtaskNameCREATEUpdater(SubtaskNameUpdaterHelper subtaskNameUpdaterHelper) {
        this.subtaskNameUpdaterHelper = subtaskNameUpdaterHelper;
    }

    @Override
    public void CREATEUpdate(Subtask subtask, SubtaskRequestBody body) {
        subtaskNameUpdaterHelper.sharedUpdate(subtask, body);
    }
}
