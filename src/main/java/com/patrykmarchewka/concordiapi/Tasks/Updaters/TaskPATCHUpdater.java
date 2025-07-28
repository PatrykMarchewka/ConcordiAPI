package com.patrykmarchewka.concordiapi.Tasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.UpdateType;

public interface TaskPATCHUpdater extends TaskUpdater {
    boolean supports(UpdateType updateType);
    void PATCHUpdate(Task task, TaskRequestBody body);
}
