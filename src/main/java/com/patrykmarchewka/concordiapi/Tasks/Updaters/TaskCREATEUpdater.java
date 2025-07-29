package com.patrykmarchewka.concordiapi.Tasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.UpdateType;

public interface TaskCREATEUpdater extends TaskUpdater {
    default boolean supports(UpdateType updateType){
        return updateType == UpdateType.CREATE;
    }
    void CREATEUpdate(Task task, TaskRequestBody body);
}
