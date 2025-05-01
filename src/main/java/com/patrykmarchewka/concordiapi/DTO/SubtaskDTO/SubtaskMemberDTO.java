package com.patrykmarchewka.concordiapi.DTO.SubtaskDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.PublicVariables;

public class SubtaskMemberDTO {
    private long id;
    private String name;
    private String description;
    private PublicVariables.TaskStatus taskStatus;


    public SubtaskMemberDTO(Subtask subtask){
        this.id = subtask.getID();
        this.name = subtask.getName();
        this.description = subtask.getDescription();
        this.taskStatus = subtask.getTaskStatus();
    }

    @Override
    public String toString(){
        return id + ":" + name + " " + description + " " + taskStatus.name();
    }

    public SubtaskMemberDTO(){}

    public long getID() {return id;}
    public void setID(long id) {this.id = id;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public PublicVariables.TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(PublicVariables.TaskStatus taskStatus) {this.taskStatus = taskStatus;}
}
