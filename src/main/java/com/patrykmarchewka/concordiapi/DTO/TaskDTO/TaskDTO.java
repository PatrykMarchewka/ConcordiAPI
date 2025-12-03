package com.patrykmarchewka.concordiapi.DTO.TaskDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskIdentity;
import com.patrykmarchewka.concordiapi.TaskStatus;

import java.time.OffsetDateTime;
import java.util.Objects;

public interface TaskDTO extends TaskIdentity {
    void setID(long ID);
    void setName(String name);
    void setDescription(String description);
    void setTaskStatus(TaskStatus taskStatus);
    void setCreationDate(OffsetDateTime creationDate);
    void setUpdateDate(OffsetDateTime updateDate);
    void setAssignedTeam(Team assignedTeam);

    default boolean equalsTask(TaskIdentity task){
        return getID() == task.getID() &&
                Objects.equals(getName(), task.getName()) &&
                Objects.equals(getDescription(), task.getDescription()) &&
                getTaskStatus() == task.getTaskStatus() &&
                Objects.equals(getCreationDate(), task.getCreationDate()) &&
                Objects.equals(getUpdateDate(), task.getUpdateDate()) &&
                Objects.equals(getAssignedTeam(), task.getAssignedTeam());
    }
}
