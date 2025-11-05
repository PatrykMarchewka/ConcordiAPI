package com.patrykmarchewka.concordiapi.DTO.SubtaskDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.HydrationContracts.Subtask.SubtaskIdentity;
import com.patrykmarchewka.concordiapi.TaskStatus;

import java.util.Objects;

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
    public long getID() {return id;}
    public void setID(long id) {this.id = id;}

    @Override
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    @Override
    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    @Override
    public Task getTask(){return task;}
    public void setTask(Task task){this.task = task;}

    @Override
    public TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(TaskStatus taskStatus) {this.taskStatus = taskStatus;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof SubtaskMemberDTO subtaskMemberDTO)) return false;
        return Objects.equals(id, subtaskMemberDTO.getID()) &&
                Objects.equals(name, subtaskMemberDTO.getName()) &&
                Objects.equals(description, subtaskMemberDTO.getDescription()) &&
                Objects.equals(taskStatus, subtaskMemberDTO.getTaskStatus());
    }

    @Override
    public int hashCode(){
        return Objects.hash(id,name,description,taskStatus);
    }
}
