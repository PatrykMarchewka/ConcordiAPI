package com.patrykmarchewka.concordiapi.DTO.SubtaskDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.HydrationContracts.Subtask.SubtaskIdentity;
import com.patrykmarchewka.concordiapi.TaskStatus;

import java.util.Objects;

public interface SubtaskDTO extends SubtaskIdentity {
    void setID(long id);
    void setName(String name);
    void setDescription(String description);
    void setTask(Task task);
    void setTaskStatus(TaskStatus taskStatus);


    default boolean equalsSubtask(SubtaskIdentity subtask){
        return getID() == subtask.getID() &&
                Objects.equals(getName(), subtask.getName()) &&
                Objects.equals(getDescription(), subtask.getDescription()) &&
                Objects.equals(getTask(), subtask.getTask()) &&
                getTaskStatus() == subtask.getTaskStatus();
    }
}
