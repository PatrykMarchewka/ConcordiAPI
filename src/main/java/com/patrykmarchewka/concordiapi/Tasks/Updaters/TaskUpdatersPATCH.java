package com.patrykmarchewka.concordiapi.Tasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.UpdateType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskUpdatersPATCH {

    private final List<TaskPATCHUpdater> updaters;

    public TaskUpdatersPATCH(List<TaskPATCHUpdater> updaters) {
        this.updaters = updaters;
    }


    /**
     * Applies PATCH updates for the Task given the TaskRequestBody details, should be only called from {@link TaskUpdatersService#update(Task, TaskRequestBody, UpdateType)}
     * @param task Task to edit
     * @param body TaskRequestBody with information to update
     */
    void applyPatchUpdates(Task task, TaskRequestBody body){
        for (TaskPATCHUpdater updater : updaters){
            updater.PATCHUpdate(task, body);
        }
    }
}
