package com.patrykmarchewka.concordiapi.Tasks.Updaters;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.UpdateType;

public interface TaskPUTUpdaterBasic extends TaskUpdater {
    default boolean supports(UpdateType updateType){
        return updateType == UpdateType.PUT;
    }
    void PUTUpdate(Task task);
}
