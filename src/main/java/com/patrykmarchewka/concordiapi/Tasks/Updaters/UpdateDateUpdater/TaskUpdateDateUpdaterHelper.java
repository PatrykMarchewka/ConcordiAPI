package com.patrykmarchewka.concordiapi.Tasks.Updaters.UpdateDateUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;

import java.time.OffsetDateTime;

public class TaskUpdateDateUpdaterHelper {

    /**
     * Shared update logic, sets the update date to current time
     * @param task Task to edit
     */
    protected void sharedUpdate(Task task){
        task.setUpdateDate(OffsetDateTime.now());
    }
}
