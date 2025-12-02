package com.patrykmarchewka.concordiapi.DTO.SubtaskDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
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


    default boolean equalsSubtask(Subtask subtask){
        return Objects.equals(getID(), subtask.getID()) &&
                Objects.equals(getName(), subtask.getName()) &&
                Objects.equals(getDescription(), subtask.getDescription()) &&
                Objects.equals(getTaskStatus(), subtask.getTaskStatus());
    }
}
