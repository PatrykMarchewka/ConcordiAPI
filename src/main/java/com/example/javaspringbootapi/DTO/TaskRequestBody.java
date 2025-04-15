package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.PublicVariables;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import java.util.Set;

public class TaskRequestBody {
    private String name;
    private String description;
    @NotNull
    private Team team;
    private Set<Integer> users;
    private PublicVariables.TaskStatus taskStatus;
    private Set<Integer> subtasks;

    public TaskRequestBody(@Nullable String name, @Nullable String description, Team team, @Nullable Set<Integer> users, @Nullable PublicVariables.TaskStatus taskStatus, @Nullable Set<Integer> subtasks){
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

    public Team getTeam(){return team;}
    public void setTeam(Team team){this.team = team;}

    public Set<Integer> getUsers(){return users;}
    public void setUsers(Set<Integer> users){this.users = users;}

    public PublicVariables.TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(PublicVariables.TaskStatus taskStatus){this.taskStatus = taskStatus;}

    public Set<Integer> getSubtasks(){return subtasks;}
    public void setSubtasks(Set<Integer> subtasks){this.subtasks = subtasks;}
}
