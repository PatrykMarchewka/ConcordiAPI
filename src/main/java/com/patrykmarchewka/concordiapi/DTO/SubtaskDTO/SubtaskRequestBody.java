package com.patrykmarchewka.concordiapi.DTO.SubtaskDTO;

import com.patrykmarchewka.concordiapi.DTO.OnPut;
import com.patrykmarchewka.concordiapi.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

public class SubtaskRequestBody {
    @NotNull(message = "{subtask.name.notnull}")
    @NotBlank(message = "{subtask.name.notblank}")
    @Size(min = 1, max = 255, message = "{size.generic}")
    private String name;

    @Size(min = 1, max = 255, message = "{size.generic}")
    private String description;

    @NotNull(groups = OnPut.class, message = "{subtask.taskstatus.notnull}")
    private TaskStatus taskStatus;

    public SubtaskRequestBody(String name,@Nullable String description,@Nullable TaskStatus taskStatus){
        this.name = name;
        this.description = description;
        this.taskStatus = (taskStatus != null) ? taskStatus : TaskStatus.NEW;
    }

    public SubtaskRequestBody(){}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(TaskStatus taskStatus){this.taskStatus = taskStatus;}
}