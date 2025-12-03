package com.patrykmarchewka.concordiapi.DTO.TaskDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskIdentity;
import com.patrykmarchewka.concordiapi.TaskStatus;

import java.time.OffsetDateTime;

public interface TaskDTO extends TaskIdentity {
    void setID(long ID);
    void setName(String name);
    void setDescription(String description);
    void setTaskStatus(TaskStatus taskStatus);
    void setCreationDate(OffsetDateTime creationDate);
    void setUpdateDate(OffsetDateTime updateDate);
    void setAssignedTeam(Team assignedTeam);
}
