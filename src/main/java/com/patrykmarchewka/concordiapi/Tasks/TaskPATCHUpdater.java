package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;

public interface TaskPATCHUpdater extends TaskUpdater{
    void PATCHUpdate(Task task, TaskRequestBody body);
}
