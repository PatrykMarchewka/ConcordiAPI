package com.patrykmarchewka.concordiapi.HydrationContracts.Subtask;

import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.TaskStatus;

public class SubtaskIdentityAdapter implements SubtaskIdentity{

    private final Subtask subtask;

    public SubtaskIdentityAdapter(final Subtask subtask){ this.subtask = subtask; }


    @Override
    public long getID() {
        return subtask.getID();
    }

    @Override
    public String getName() {
        return subtask.getName();
    }

    @Override
    public String getDescription() {
        return subtask.getDescription();
    }

    @Override
    public TaskStatus getTaskStatus() {
        return subtask.getTaskStatus();
    }
}
