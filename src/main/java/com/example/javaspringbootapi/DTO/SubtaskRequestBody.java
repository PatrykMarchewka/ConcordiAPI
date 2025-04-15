package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.PublicVariables;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

public class SubtaskRequestBody {
    private String name;
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
