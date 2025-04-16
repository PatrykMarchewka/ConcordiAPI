package com.example.javaspringbootapi.DTO.SubtaskDTO;

import com.example.javaspringbootapi.DTO.OnCreate;
import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.PublicVariables;
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
    private Task task;
    private PublicVariables.TaskStatus taskStatus;

    public SubtaskRequestBody(@Nullable String name,@Nullable String description, Task task,@Nullable PublicVariables.TaskStatus taskStatus){
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

    public Task getTask(){return task;}
    public void setTask(Task task){this.task = task;}

    public PublicVariables.TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(PublicVariables.TaskStatus taskStatus){this.taskStatus = taskStatus;}
}
