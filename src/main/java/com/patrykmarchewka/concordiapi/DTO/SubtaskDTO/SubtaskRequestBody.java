package com.patrykmarchewka.concordiapi.DTO.SubtaskDTO;

import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.PublicVariables;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

@JsonIgnoreProperties()
public class SubtaskRequestBody {
    @NotBlank(groups = OnCreate.class)
    private String name;
    @NotBlank(groups = OnCreate.class)
    private String description;
    @NotNull
    private long task;
    private PublicVariables.TaskStatus taskStatus;

    public SubtaskRequestBody(@Nullable String name,@Nullable String description, long task,@Nullable PublicVariables.TaskStatus taskStatus){
        this.name = name;
        this.description = description;
        this.task = task;
        this.taskStatus = taskStatus;
    }

    public SubtaskRequestBody(){}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public long getTask(){return task;}
    public void setTask(long task){this.task = task;}

    public PublicVariables.TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(PublicVariables.TaskStatus taskStatus){this.taskStatus = taskStatus;}
}
