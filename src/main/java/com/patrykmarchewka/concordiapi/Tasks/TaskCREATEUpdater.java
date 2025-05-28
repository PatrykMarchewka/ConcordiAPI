package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;

public interface TaskCREATEUpdater extends TaskUpdater {
    void CREATEUpdate(Task task, TaskRequestBody body);
}
