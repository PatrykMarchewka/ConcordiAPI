package com.patrykmarchewka.concordiapi.Tasks.Updaters;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.UpdateType;

public interface TaskPATCHUpdaterBasic extends TaskUpdater {
    default boolean supports(UpdateType updateType){
        return updateType == UpdateType.PATCH;
    }
    void PATCHUpdate(Task task);
}
