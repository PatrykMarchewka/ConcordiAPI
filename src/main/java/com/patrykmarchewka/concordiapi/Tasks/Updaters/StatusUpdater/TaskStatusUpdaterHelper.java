package com.patrykmarchewka.concordiapi.Tasks.Updaters.StatusUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusUpdaterHelper {
    void sharedUpdate(Task task, TaskRequestBody body){ task.setTaskStatus(body.getTaskStatus()); }
}
