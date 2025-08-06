package com.patrykmarchewka.concordiapi.Tasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskUpdatersCREATE {
    private final List<TaskCREATEUpdater> updaters;
    private final List<TaskCREATEUpdaterBasic> updaterBasics;

    @Autowired
    public TaskUpdatersCREATE(List<TaskCREATEUpdater> updaters, List<TaskCREATEUpdaterBasic> updaterBasics) {
        this.updaters = updaters;
        this.updaterBasics = updaterBasics;
    }

    /**
     * Applies CREATE updates for the Task given the TaskRequestBody details, should be only called from {@link TaskUpdatersService#update(Task, TaskRequestBody, UpdateType)}
     * @param task Task to create
     * @param body TaskRequestBody with information to update
     */
    void applyCreateUpdates(Task task, TaskRequestBody body){
        for (TaskCREATEUpdater updater : updaters){
            updater.CREATEUpdate(task, body);
        }

        for (TaskCREATEUpdaterBasic updaterBasic : updaterBasics){
            updaterBasic.CREATEUpdate(task);
        }
    }

}
