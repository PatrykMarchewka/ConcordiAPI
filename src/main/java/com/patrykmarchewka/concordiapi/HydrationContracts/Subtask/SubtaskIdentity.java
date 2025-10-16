package com.patrykmarchewka.concordiapi.HydrationContracts.Subtask;

import com.patrykmarchewka.concordiapi.TaskStatus;

public interface SubtaskIdentity {
    long getID();
    String getName();
    String getDescription();
    TaskStatus getTaskStatus();
}
