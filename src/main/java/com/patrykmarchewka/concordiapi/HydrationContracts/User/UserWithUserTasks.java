package com.patrykmarchewka.concordiapi.HydrationContracts.User;

import com.patrykmarchewka.concordiapi.DatabaseModel.UserTask;

import java.util.Set;

public interface UserWithUserTasks extends UserIdentity{
    Set<UserTask> getUserTasks();
}
