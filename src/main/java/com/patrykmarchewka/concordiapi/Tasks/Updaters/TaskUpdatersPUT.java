package com.patrykmarchewka.concordiapi.Tasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskUpdatersPUT {
    private final List<TaskPUTUpdater> updaters;

    @Autowired
    public TaskUpdatersPUT(List<TaskPUTUpdater> updaters) {
        this.updaters = updaters;
    }


    /**
     * Applies PUT updates for the Task given the TaskRequestBody details, should be only called from {@link TaskUpdatersService#update(Task, TaskRequestBody, UpdateType)}
     * @param task Task to edit
     * @param body TaskRequestBody with information to update
     */
    void applyPutUpdates(Task task, TaskRequestBody body){
        for (TaskPUTUpdater updater : updaters){
            updater.PUTUpdate(task, body);
        }
    }
}
