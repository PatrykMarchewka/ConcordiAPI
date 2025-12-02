package com.patrykmarchewka.concordiapi.DTO.TaskDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskFull;
import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.TaskStatus;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@JsonPropertyOrder({"ID", "Name", "Description", "Task status", "Subtasks", "Users", "Creation date", "Update date"})
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

    @Override
    @JsonProperty("ID")
    public long getID() {return id;}
    @Override
    public void setID(long id) {this.id = id;}

    @Override
    @JsonProperty("Name")
    public String getName(){return name;}
    @Override
    public void setName(String name) {this.name = name;}

    @Override
    @JsonProperty("Description")
    public String getDescription(){return description;}
    @Override
    public void setDescription(String description){this.description = description;}

    @Override
    @JsonProperty("Task status")
    public TaskStatus getTaskStatus(){return taskStatus;}
    @Override
    public void setTaskStatus(TaskStatus taskStatus){this.taskStatus = taskStatus;}

    @JsonProperty("Subtasks")
    public Set<SubtaskMemberDTO> getSubtasks(){return subtasks;}
    public void setSubtasks(Set<SubtaskMemberDTO> subtasks){this.subtasks = subtasks;}

    @JsonProperty("Users")
    public Set<UserMemberDTO> getUsers(){return users;}
    public void setUsers(Set<UserMemberDTO> users){this.users = users;}

    @Override
    public OffsetDateTime getCreationDate(){return creationDate;}
    @Override
    public void setCreationDate(OffsetDateTime creationDate){this.creationDate = creationDate;}

    @Override
    public OffsetDateTime getUpdateDate(){return updateDate;}
    @Override
    public void setUpdateDate(OffsetDateTime updateDate){this.updateDate = updateDate;}

    @Override
    public Team getAssignedTeam(){return assignedTeam;}
    @Override
    public void setAssignedTeam(Team assignedTeam){this.assignedTeam = assignedTeam;}

    @JsonProperty("Creation date")
    public String getCreationDateString(){return OffsetDateTimeConverter.formatDate(this.creationDate);}
    public void setCreationDateString(String creationDateString){this.creationDate = OffsetDateTimeConverter.parseDate(creationDateString);}
    @JsonProperty("Update date")
    public String getUpdateDateString(){return OffsetDateTimeConverter.formatDate(this.updateDate);}
    public void setUpdateDateString(String updateDateString){this.updateDate = OffsetDateTimeConverter.parseDate(updateDateString);}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof TaskMemberDTO taskMemberDTO)) return false;
        return id == taskMemberDTO.id &&
                Objects.equals(name, taskMemberDTO.name) &&
                Objects.equals(description, taskMemberDTO.description) &&
                taskStatus == taskMemberDTO.taskStatus &&
                Objects.equals(subtasks, taskMemberDTO.subtasks) &&
                Objects.equals(users, taskMemberDTO.users) &&
                Objects.equals(creationDate, taskMemberDTO.creationDate) &&
                Objects.equals(updateDate, taskMemberDTO.updateDate) &&
                Objects.equals(assignedTeam, taskMemberDTO.assignedTeam);

    }

    @Override
    public int hashCode(){ return Objects.hash(id,name,description,taskStatus, subtasks, users, creationDate, updateDate, assignedTeam); }
}
