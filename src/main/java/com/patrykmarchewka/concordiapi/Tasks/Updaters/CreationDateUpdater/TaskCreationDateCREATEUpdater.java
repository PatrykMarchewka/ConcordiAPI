package com.patrykmarchewka.concordiapi.Tasks.Updaters.CreationDateUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskCREATEUpdaterBasic;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class TaskCreationDateCREATEUpdater implements TaskCREATEUpdaterBasic {
    @Override
    public void CREATEUpdate(Task task) {
        task.setCreationDate(OffsetDateTime.now());
    }
}
