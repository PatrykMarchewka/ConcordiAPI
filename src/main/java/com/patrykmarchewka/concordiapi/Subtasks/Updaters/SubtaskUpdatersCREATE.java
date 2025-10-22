package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class SubtaskUpdatersCREATE {
    private final List<SubtaskCREATEUpdater> updaters;
    private final List<SubtaskCREATEUpdaterBasic> updaterBasics;
    private final List<SubtaskCREATEUpdaterBasicWithTask> updaterBasicWithTasks;

    @Autowired
    public SubtaskUpdatersCREATE(List<SubtaskCREATEUpdater> updaters, List<SubtaskCREATEUpdaterBasic> updaterBasics, List<SubtaskCREATEUpdaterBasicWithTask> updaterBasicWithTasks) {
        this.updaters = updaters;
        this.updaterBasics = updaterBasics;
        this.updaterBasicWithTasks = updaterBasicWithTasks;
    }


    /**
     * Applies CREATE updates for the Subtask given the SubtaskRequestBody details, should only be called from {@link SubtaskUpdatersService#createUpdate(Subtask, SubtaskRequestBody, Supplier)}
     * @param subtask Subtask to create
     * @param body SubtaskRequestBody with new values
     * @param task Task to attach subtask to
     */
    void applyCreateUpdates(Subtask subtask, SubtaskRequestBody body, Supplier<Task> task){
        for (SubtaskCREATEUpdater updater : updaters){
            updater.CREATEUpdate(subtask,body);
        }

        for (SubtaskCREATEUpdaterBasicWithTask updaterBasicWithTask : updaterBasicWithTasks){
            updaterBasicWithTask.setTask(task.get());
            updaterBasicWithTask.CREATEUpdate(subtask);
        }

        for (SubtaskCREATEUpdaterBasic updaterBasic : updaterBasics){
            updaterBasic.CREATEUpdate(subtask);
        }
    }
}
