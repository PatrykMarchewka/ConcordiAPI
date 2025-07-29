package com.patrykmarchewka.concordiapi.Tasks.Updaters.CreationDateUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskCREATEUpdaterBasic;

import java.time.OffsetDateTime;

public class TaskCreationDateCREATEUpdater implements TaskCREATEUpdaterBasic {
    @Override
    public void CREATEUpdate(Task task) {
        task.setCreationDate(OffsetDateTime.now());
    }
}
