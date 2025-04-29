package com.example.javaspringbootapi.DTO.TaskDTO;

import com.example.javaspringbootapi.DTO.OnCreate;
import com.example.javaspringbootapi.DTO.TeamDTO.TeamMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.PublicVariables;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private TeamMemberDTO teamMemberDTO;
    private Set<Integer> users;
    private PublicVariables.TaskStatus taskStatus;
    private Set<Integer> subtasks;

    public TaskRequestBody(String name, @Nullable String description, TeamMemberDTO teamMemberDTO, @Nullable Set<Integer> users, @Nullable PublicVariables.TaskStatus taskStatus, @Nullable Set<Integer> subtasks){
        this.name = name;
        this.description = description;
        this.teamMemberDTO = teamMemberDTO;
        this.users = users;
        this.taskStatus = taskStatus;
        this.subtasks = subtasks;
    }

    public TaskRequestBody(){}

    public String getName(){return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public TeamMemberDTO getTeam(){return teamMemberDTO;}
    public void setTeam(TeamMemberDTO teamMemberDTO){this.teamMemberDTO = teamMemberDTO;}

    public Set<Integer> getUsers(){return users;}
    public void setUsers(Set<Integer> users){this.users = users;}

    public PublicVariables.TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(PublicVariables.TaskStatus taskStatus){this.taskStatus = taskStatus;}

    public Set<Integer> getSubtasks(){return subtasks;}
    public void setSubtasks(Set<Integer> subtasks){this.subtasks = subtasks;}
}
