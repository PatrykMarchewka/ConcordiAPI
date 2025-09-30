package com.patrykmarchewka.concordiapi.DTO.SubtaskDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.TaskStatus;

import java.util.Objects;

public interface SubtaskDTO {
    long getID();
    String getName();
    String getDescription();
    TaskStatus getTaskStatus();


    default boolean equalsSubtask(Subtask subtask){
        return Objects.equals(getID(), subtask.getID()) &&
                Objects.equals(getName(), subtask.getName()) &&
                Objects.equals(getDescription(), subtask.getDescription()) &&
                Objects.equals(getTaskStatus(), subtask.getTaskStatus());
    }
}
