package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;

public interface TaskPUTUpdater extends TaskUpdater{
    void PUTUpdate(Task task, TaskRequestBody body);
}
