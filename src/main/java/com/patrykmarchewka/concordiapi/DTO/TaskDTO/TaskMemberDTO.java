package com.patrykmarchewka.concordiapi.DTO.TaskDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskFull;
import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.TaskStatus;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TaskMemberDTO implements TaskDTO {
    private long id;
    private String name;
    private String description;
    private TaskStatus taskStatus;
    private Set<SubtaskMemberDTO> subtasks = new HashSet<>();
    private Set<UserMemberDTO> users = new HashSet<>();
    @JsonIgnore
    private OffsetDateTime creationDate;
    @JsonIgnore
    private OffsetDateTime updateDate;
    @JsonIgnore
    private Team assignedTeam;

    public TaskMemberDTO(Task task){
        this.id = task.getID();
        this.name = task.getName();
        this.description = task.getDescription();
        this.taskStatus = task.getTaskStatus();
        for (User user : task.getUsers()){
            users.add(new UserMemberDTO(user));
        }
        for (Subtask subtask : task.getSubtasks()){
            subtasks.add(new SubtaskMemberDTO(subtask));
        }
        this.creationDate = task.getCreationDate();
        this.updateDate = task.getUpdateDate();
        this.assignedTeam = task.getAssignedTeam();
    }

    public TaskMemberDTO(TaskFull task){
        this.id = task.getID();
        this.name = task.getName();
        this.description = task.getDescription();
        this.taskStatus = task.getTaskStatus();
        for (User user : task.getUsers()){
            users.add(new UserMemberDTO(user));
        }
        for (Subtask subtask : task.getSubtasks()){
            subtasks.add(new SubtaskMemberDTO(subtask));
        }
        this.creationDate = task.getCreationDate();
        this.updateDate = task.getUpdateDate();
        this.assignedTeam = task.getAssignedTeam();
    }


    public TaskMemberDTO(){}

    public long getID() {return id;}
    public void setID(long id) {this.id = id;}

    public String getName(){return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(TaskStatus taskStatus){this.taskStatus = taskStatus;}

    public Set<SubtaskMemberDTO> getSubtasks(){return subtasks;}
    public void setSubtasks(Set<SubtaskMemberDTO> subtasks){this.subtasks = subtasks;}

    public Set<UserMemberDTO> getUsers(){return users;}
    public void setUsers(Set<UserMemberDTO> users){this.users = users;}

    public OffsetDateTime getCreationDate(){return creationDate;}
    public void setCreationDate(OffsetDateTime creationDate){this.creationDate = creationDate;}

    public OffsetDateTime getUpdateDate(){return updateDate;}
    public void setUpdateDate(OffsetDateTime updateDate){this.updateDate = updateDate;}

    public Team getAssignedTeam(){return assignedTeam;}
    public void setAssignedTeam(Team assignedTeam){this.assignedTeam = assignedTeam;}

    @JsonProperty("creationDate")
    public String getCreationDateString(){return OffsetDateTimeConverter.formatDate(this.creationDate);}
    @JsonProperty("updateDate")
    public String getUpdateDateString(){return OffsetDateTimeConverter.formatDate(this.updateDate);}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof TaskMemberDTO taskMemberDTO)) return false;
        return Objects.equals(id, taskMemberDTO.getID()) &&
                Objects.equals(name, taskMemberDTO.getName()) &&
                Objects.equals(description, taskMemberDTO.getDescription()) &&
                Objects.equals(taskStatus, taskMemberDTO.getTaskStatus()) &&
                Objects.equals(subtasks, taskMemberDTO.getSubtasks()) &&
                Objects.equals(users, taskMemberDTO.getUsers());

    }

    @Override
    public int hashCode(){ return Objects.hash(id,name,description,taskStatus, subtasks, users); }
}
