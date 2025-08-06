package com.patrykmarchewka.concordiapi.Tasks.Updaters.SubtaskUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Subtasks.SubtaskService;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class TaskSubtaskUpdaterHelper {

    private final SubtaskService subtaskService;
    private final TaskService taskService;

    @Autowired
    public TaskSubtaskUpdaterHelper(SubtaskService subtaskService, @Lazy TaskService taskService) {
        this.subtaskService = subtaskService;
        this.taskService = taskService;
    }

    void sharedUpdate(Task task, TaskRequestBody body){
        removeSubtasks(task);
        addSubtasks(task, body);
    }

    void removeSubtasks(Task task){
        if (task.getSubtasks() != null){
            for (Subtask subtask : task.getSubtasks()){
                taskService.removeSubtaskFromTaskAndDelete(task, subtask);
            }
        }
    }

    void addSubtasks(Task task, TaskRequestBody body){
        for (int id : body.getSubtasks()){
            taskService.addSubtaskToTask(task, subtaskService.getSubtaskByID(task.getID(), id));
        }
    }
}
