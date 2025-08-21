package com.patrykmarchewka.concordiapi.Subtasks.Updaters.TaskUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskCREATEUpdaterBasicWithTask;
import org.springframework.stereotype.Component;

@Component
public class SubtaskTaskCREATEUpdater implements SubtaskCREATEUpdaterBasicWithTask {

    private Task task;

    @Override
    public void CREATEUpdate(Subtask subtask) {
        if (this.task == null){
            throw new BadRequestException("The task is set to null");
        }
        subtask.setTask(task);
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }


}
