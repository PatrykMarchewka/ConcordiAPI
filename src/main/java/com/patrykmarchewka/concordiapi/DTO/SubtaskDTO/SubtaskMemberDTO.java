package com.patrykmarchewka.concordiapi.DTO.SubtaskDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.HydrationContracts.Subtask.SubtaskIdentity;
import com.patrykmarchewka.concordiapi.TaskStatus;

import java.util.Objects;

@JsonPropertyOrder({"ID", "Name", "Description", "Subtask status"})
public class SubtaskMemberDTO implements SubtaskDTO{
    private long id;
    private String name;
    private String description;
    @JsonIgnore
    private Task task;
    private TaskStatus taskStatus;

    public SubtaskMemberDTO(SubtaskIdentity subtask){
        this.id = subtask.getID();
        this.name = subtask.getName();
        this.description = subtask.getDescription();
        this.task = subtask.getTask();
        this.taskStatus = subtask.getTaskStatus();
    }

    public SubtaskMemberDTO(){}

    @Override
    @JsonProperty("ID")
    public long getID() {return id;}
    @Override
    public void setID(long id) {this.id = id;}

    @Override
    @JsonProperty("Name")
    public String getName(){return name;}
    @Override
    public void setName(String name){this.name = name;}

    @Override
    @JsonProperty("Description")
    public String getDescription(){return description;}
    @Override
    public void setDescription(String description){this.description = description;}

    @Override
    public Task getTask(){return task;}
    @Override
    public void setTask(Task task){this.task = task;}

    @Override
    @JsonProperty("Subtask status")
    public TaskStatus getTaskStatus(){return taskStatus;}
    @Override
    public void setTaskStatus(TaskStatus taskStatus) {this.taskStatus = taskStatus;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof SubtaskMemberDTO subtaskMemberDTO)) return false;
        return id == subtaskMemberDTO.id &&
                Objects.equals(name, subtaskMemberDTO.name) &&
                Objects.equals(description, subtaskMemberDTO.description) &&
                Objects.equals(task, subtaskMemberDTO.task) &&
                taskStatus == subtaskMemberDTO.taskStatus;
    }

    @Override
    public int hashCode(){
        return Objects.hash(id,name,description,task,taskStatus);
    }
}
