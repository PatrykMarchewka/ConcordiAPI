package com.patrykmarchewka.concordiapi.Tasks.Updaters;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.UpdateType;

public interface TaskPUTUpdaterBasic extends TaskUpdater {
    boolean supports(UpdateType updateType);
    void PUTUpdate(Task task);
}
