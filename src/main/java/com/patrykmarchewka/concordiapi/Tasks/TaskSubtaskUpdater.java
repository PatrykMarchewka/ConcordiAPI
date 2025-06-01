package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Subtasks.SubtaskService;

public class TaskSubtaskUpdater implements TaskCREATEUpdater,TaskPUTUpdater,TaskPATCHUpdater{

    private final SubtaskService subtaskService;
    private final TaskService taskService;

    public TaskSubtaskUpdater(SubtaskService subtaskService, TaskService taskService) {
        this.subtaskService = subtaskService;
        this.taskService = taskService;
    }

    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        sharedUpdate(task, body);
    }

    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        for (Subtask subtask : task.getSubtasks()){
            taskService.removeSubtaskFromTaskAndDelete(task, subtask);
        }
        for (int id : body.getSubtasks()){
            taskService.addSubtaskToTask(task, subtaskService.getSubtaskByID(task.getID(), id));
        }
    }

    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {
        sharedUpdate(task, body);
    }

    private void sharedUpdate(Task task, TaskRequestBody body){
        if (task.getSubtasks() != null){
            for (Subtask subtask : task.getSubtasks()){
                taskService.removeSubtaskFromTaskAndDelete(task, subtask);
            }
        }
        for (int id : body.getSubtasks()){
            taskService.addSubtaskToTask(task, subtaskService.getSubtaskByID(task.getID(), id));
        }
    }
}
