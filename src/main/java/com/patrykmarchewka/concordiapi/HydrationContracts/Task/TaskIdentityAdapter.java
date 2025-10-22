package com.patrykmarchewka.concordiapi.HydrationContracts.Task;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.TaskStatus;

import java.time.OffsetDateTime;

public class TaskIdentityAdapter implements TaskIdentity{

    private final Task task;

    public TaskIdentityAdapter(Task task){ this.task = task; }

    @Override
    public long getID() {
        return task.getID();
    }

    @Override
    public String getName() {
        return task.getName();
    }

    @Override
    public String getDescription() {
        return task.getDescription();
    }

    @Override
    public TaskStatus getTaskStatus() {
        return task.getTaskStatus();
    }

    @Override
    public OffsetDateTime getCreationDate() {
        return task.getCreationDate();
    }

    @Override
    public OffsetDateTime getUpdateDate() {
        return task.getUpdateDate();
    }

    @Override
    public Team getAssignedTeam() {
        return task.getAssignedTeam();
    }
}
