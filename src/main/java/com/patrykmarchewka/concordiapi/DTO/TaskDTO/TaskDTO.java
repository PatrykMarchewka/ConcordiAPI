package com.patrykmarchewka.concordiapi.DTO.TaskDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskIdentity;

import java.util.Objects;

public interface TaskDTO extends TaskIdentity {
    /**
     * @deprecated Will be replaced by {@link #equalsTask(TaskIdentity)}
     * @param task
     * @return
     */
    @Deprecated
    default boolean equalsTask(Task task){
        return Objects.equals(getID(), task.getID()) &&
                Objects.equals(getName(), task.getName()) &&
                Objects.equals(getDescription(), task.getDescription()) &&
                Objects.equals(getTaskStatus(), task.getTaskStatus());
    }

    default boolean equalsTask(TaskIdentity task){
        return Objects.equals(getID(), task.getID()) &&
                Objects.equals(getName(), task.getName()) &&
                Objects.equals(getDescription(), task.getDescription()) &&
                Objects.equals(getTaskStatus(), task.getTaskStatus());
    }
}
