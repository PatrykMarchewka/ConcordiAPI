package com.patrykmarchewka.concordiapi.Tasks.Updaters.UserUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.Users.UserService;

import java.util.Set;

public class TaskUserUpdaterHelper {
    protected final TaskService taskService;
    protected final UserService userService;

    protected TaskUserUpdaterHelper(UserService userService, TaskService taskService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    /**
     * Shared logic for changing task users, removes all existing users from task then adds those from body
     * @param task Task to edit
     * @param userIds Set of Integers containing user IDs to add
     */
    protected void sharedUpdate(Task task, Set<Integer> userIds) {
        taskService.removeUsersFromTask(task);
        taskService.addUsersToTask(task, userService.getUsersFromIDs(userIds));
    }
}
