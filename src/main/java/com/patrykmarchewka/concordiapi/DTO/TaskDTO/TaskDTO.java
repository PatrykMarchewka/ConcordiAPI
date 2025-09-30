package com.patrykmarchewka.concordiapi.DTO.TaskDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.TaskStatus;

import java.util.Objects;

public interface TaskDTO {
    long getID();
    String getName();
    String getDescription();
    TaskStatus getTaskStatus();


    default boolean equalsTask(Task task){
        return Objects.equals(getID(), task.getID()) &&
                Objects.equals(getName(), task.getName()) &&
                Objects.equals(getDescription(), task.getDescription()) &&
                Objects.equals(getTaskStatus(), task.getTaskStatus());
    }
}
