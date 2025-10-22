package com.patrykmarchewka.concordiapi.HydrationContracts.Task;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.TaskStatus;

import java.time.OffsetDateTime;

public interface TaskIdentity {
    long getID();
    String getName();
    String getDescription();
    TaskStatus getTaskStatus();
    OffsetDateTime getCreationDate();
    OffsetDateTime getUpdateDate();
    Team getAssignedTeam();
}
