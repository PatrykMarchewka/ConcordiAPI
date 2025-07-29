package com.patrykmarchewka.concordiapi.Tasks.Updaters.StatusUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.TaskStatus;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskCREATEUpdater;

public class TaskStatusCREATEUpdater implements TaskCREATEUpdater {

    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        task.setTaskStatus((body.getTaskStatus() == null ? TaskStatus.NEW : body.getTaskStatus()));
    }
}
