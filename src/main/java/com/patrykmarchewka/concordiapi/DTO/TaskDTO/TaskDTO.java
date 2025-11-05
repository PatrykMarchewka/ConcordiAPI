package com.patrykmarchewka.concordiapi.DTO.TaskDTO;

import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskIdentity;

import java.util.Objects;

public interface TaskDTO extends TaskIdentity {

    default boolean equalsTask(TaskIdentity task){
        return Objects.equals(getID(), task.getID()) &&
                Objects.equals(getName(), task.getName()) &&
                Objects.equals(getDescription(), task.getDescription()) &&
                Objects.equals(getTaskStatus(), task.getTaskStatus());
    }
}
