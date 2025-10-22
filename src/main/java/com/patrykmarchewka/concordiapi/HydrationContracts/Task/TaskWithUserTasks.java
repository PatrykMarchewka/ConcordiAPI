package com.patrykmarchewka.concordiapi.HydrationContracts.Task;

import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.DatabaseModel.UserTask;

import java.util.Set;
import java.util.stream.Collectors;

public interface TaskWithUserTasks extends TaskIdentity{
    Set<UserTask> getUserTasks();
    default Set<User> getUsers() {return getUserTasks().stream().map(UserTask::getAssignedUser).collect(Collectors.toUnmodifiableSet());}
    default boolean hasUser(User user){return getUserTasks().stream().map(UserTask::getAssignedUser).anyMatch(u -> u.equals(user));}

}
