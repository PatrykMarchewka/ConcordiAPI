package com.patrykmarchewka.concordiapi.DatabaseModel;

public interface UserTaskTestHelper {
    default UserTask createUserTask(User user, Task task, UserTaskRepository userTaskRepository){
        UserTask userTask = new UserTask();
        userTask.setAssignedUser(user);
        userTask.setAssignedTask(task);

        return userTaskRepository.save(userTask);
    }
}
