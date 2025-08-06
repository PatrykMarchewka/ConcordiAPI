package com.patrykmarchewka.concordiapi.Subtasks.Updaters.TaskUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class SubtaskTaskUpdaterHelper {

    private final TaskService taskService;


    @Autowired
    public SubtaskTaskUpdaterHelper(@Lazy TaskService taskService) {
        this.taskService = taskService;
    }


    void sharedUpdate(Subtask subtask, SubtaskRequestBody body, Team team){
        if (subtask.getTask() != null){
            taskService.removeSubtaskFromTask(subtask.getTask(), subtask);
        }
        taskService.addSubtaskToTask(taskService.getTaskByIDAndTeam(body.getTask(), team),subtask);
    }
}
