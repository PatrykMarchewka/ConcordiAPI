package com.patrykmarchewka.concordiapi.Tasks.Updaters.DescriptionUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskCREATEUpdater;
import org.springframework.stereotype.Component;

@Component
public class TaskDescriptionCREATEUpdater implements TaskCREATEUpdater {
    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        task.setDescription((body.getDescription() == null ? null : body.getDescription()));
    }
}
