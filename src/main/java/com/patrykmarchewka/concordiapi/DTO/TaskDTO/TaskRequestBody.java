package com.patrykmarchewka.concordiapi.DTO.TaskDTO;

import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.patrykmarchewka.concordiapi.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import java.util.Set;

@JsonIgnoreProperties()
public class TaskRequestBody {
    @NotBlank(groups = OnCreate.class)
    private String name;
    private String description;
    @NotNull
    private long team;
    private Set<Integer> users;
    private TaskStatus taskStatus;
    private Set<Integer> subtasks;

    public TaskRequestBody(String name, @Nullable String description, long team, @Nullable Set<Integer> users, @Nullable TaskStatus taskStatus, @Nullable Set<Integer> subtasks){
        this.name = name;
        this.description = description;
        this.team = team;
        this.users = users;
        this.taskStatus = taskStatus;
        this.subtasks = subtasks;
    }

    public TaskRequestBody(){}

    public String getName(){return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public long getTeam(){return team;}
    public void setTeam(long team){this.team = team;}

    public Set<Integer> getUsers(){return users;}
    public void setUsers(Set<Integer> users){this.users = users;}

    public TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(TaskStatus taskStatus){this.taskStatus = taskStatus;}

    public Set<Integer> getSubtasks(){return subtasks;}
    public void setSubtasks(Set<Integer> subtasks){this.subtasks = subtasks;}
}
