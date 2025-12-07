package com.patrykmarchewka.concordiapi.Tasks.Updaters.UserUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TaskUserUpdaterHelper {
    protected final UserService userService;

    @Autowired
    protected TaskUserUpdaterHelper(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Shared logic for changing task users, removes all existing users from task then adds those from body
     * @param task Task to edit
     * @param userIDs Set of Integers containing user IDs to add
     */
    protected void sharedUpdate(Task task, Set<Integer> userIDs) {
        task.getUserTasks().clear();
        for (int userID : userIDs){
            task.addUserTask((User) userService.getUserWithUserTasks(userID));
        }
    }
}
