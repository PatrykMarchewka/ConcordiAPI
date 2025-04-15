package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Subtask;
import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.User;
import com.example.javaspringbootapi.PublicVariables;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class TaskManagerDTO {
    private long id;
    private String name;
    private String description;
    private PublicVariables.TaskStatus taskStatus;
    private Set<SubtaskMemberDTO> subtasks = new HashSet<>();
    private Set<UserMemberDTO> users = new HashSet<>();
    private String creationDate;
    private String updateDate;

    public TaskManagerDTO(Task task){
        this.id = task.getID();
        this.name = task.getName();
        this.description = task.getDescription();
        this.taskStatus = task.getTaskStatus();
        this.creationDate = task.getCreationDate().toString();
        this.updateDate = task.getUpdateDate().toString();
        for (User user : task.getUsers()){
            users.add(new UserMemberDTO(user));
        }
        for (Subtask subtask : task.getSubtasks()){
            subtasks.add(new SubtaskMemberDTO(subtask));
        }
    }

    public TaskManagerDTO(){}

    public long getID() {return id;}
    public void setID(long id) {this.id = id;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public PublicVariables.TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(PublicVariables.TaskStatus taskStatus){this.taskStatus = taskStatus;}

    public Set<SubtaskMemberDTO> getSubtasks(){return subtasks;}
    public void setSubtasks(Set<SubtaskMemberDTO> subtasks){this.subtasks = subtasks;}

    public Set<UserMemberDTO> getUsers(){return users;}
    public void setUsers(Set<UserMemberDTO> users){this.users = users;}

    public String getCreationDate(){return creationDate;}
    public void setCreationDate(OffsetDateTime creationDate){this.creationDate = creationDate.toString();}

    public String getUpdateDate(){return updateDate;}
    public void setUpdateDate(OffsetDateTime updateDate){this.updateDate = updateDate.toString();}
}
