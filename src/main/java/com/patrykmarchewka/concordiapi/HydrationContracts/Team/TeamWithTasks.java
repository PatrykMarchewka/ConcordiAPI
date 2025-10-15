package com.patrykmarchewka.concordiapi.HydrationContracts.Team;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;

import java.util.Set;

public interface TeamWithTasks extends TeamIdentity{
    Set<Task> getTeamTasks();
}
