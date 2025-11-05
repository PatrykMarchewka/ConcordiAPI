package com.patrykmarchewka.concordiapi.HydrationContracts.Subtask;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.TaskStatus;

public interface SubtaskIdentity {
    long getID();
    String getName();
    String getDescription();
    Task getTask();
    TaskStatus getTaskStatus();
}
