package com.patrykmarchewka.concordiapi.HydrationContracts.Task;

import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;

import java.util.Set;

public interface TaskWithSubtasks extends TaskIdentity{
    Set<Subtask> getSubtasks();
}
