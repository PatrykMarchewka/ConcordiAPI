package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Users.UserService;

public class TaskUserUpdater implements TaskCREATEUpdater,TaskPUTUpdater,TaskPATCHUpdater{

    private final UserService userService;
    private final TaskService taskService;

    public TaskUserUpdater(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }


    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {

    }

    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        taskService.removeUsersFromTask(task);
        taskService.addUsersToTask(task,userService.getUsersFromIDs(body.getUsers()));
    }

    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body) {
        if (body.getUsers() != null){
            taskService.removeUsersFromTask(task);
            taskService.addUsersToTask(task,userService.getUsersFromIDs(body.getUsers()));
        }
    }
}
