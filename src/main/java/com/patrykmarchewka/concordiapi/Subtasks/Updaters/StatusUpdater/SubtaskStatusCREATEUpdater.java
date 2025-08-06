package com.patrykmarchewka.concordiapi.Subtasks.Updaters.StatusUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskCREATEUpdater;
import com.patrykmarchewka.concordiapi.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class SubtaskStatusCREATEUpdater implements SubtaskCREATEUpdater {

    @Override
    public void CREATEUpdate(Subtask subtask, SubtaskRequestBody body) {
        subtask.setTaskStatus((body.getTaskStatus() == null ? TaskStatus.NEW : body.getTaskStatus()));
    }
}
