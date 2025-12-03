package com.patrykmarchewka.concordiapi.DTO.SubtaskDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.HydrationContracts.Subtask.SubtaskIdentity;
import com.patrykmarchewka.concordiapi.TaskStatus;

public interface SubtaskDTO extends SubtaskIdentity {
    void setID(long id);
    void setName(String name);
    void setDescription(String description);
    void setTask(Task task);
    void setTaskStatus(TaskStatus taskStatus);
}
