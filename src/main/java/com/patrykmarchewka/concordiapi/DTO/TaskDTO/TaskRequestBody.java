package com.patrykmarchewka.concordiapi.DTO.TaskDTO;

import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.OnPut;
import com.patrykmarchewka.concordiapi.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

import java.util.HashSet;
import java.util.Set;

public class TaskRequestBody {
    @NotNull(groups = {OnCreate.class, OnPut.class},message = "{notnull.generic}")
    @NotBlank(groups = {OnCreate.class, OnPut.class},message = "{notblank.generic}")
    @Size(min = 1, max = 255, message = "{size.generic}")
    private String name;

    @NotBlank(groups = OnPut.class, message = "{notblank.generic}")
    @Size(min = 1, max = 255, message = "{size.generic}")
    private String description;

    @NotNull(groups = OnPut.class, message = "{notnull.generic}")
    private Set<Integer> users = new HashSet<>();

    @NotNull(groups = OnPut.class, message = "{notnull.generic}")
    private TaskStatus taskStatus;

    public TaskRequestBody(String name, @Nullable String description, @Nullable Set<Integer> users, @Nullable TaskStatus taskStatus){
        this.name = name;
        this.description = description;
        this.users = users;
        this.taskStatus = (taskStatus != null) ? taskStatus : TaskStatus.NEW;
    }

    public TaskRequestBody(){}

    public String getName(){return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public Set<Integer> getUsers(){return users;}
    public void setUsers(Set<Integer> users){this.users = users;}

    public TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(TaskStatus taskStatus){this.taskStatus = taskStatus;}
}
