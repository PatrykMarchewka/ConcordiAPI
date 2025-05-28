package com.patrykmarchewka.concordiapi.DTO.TaskDTO;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.PublicVariables;

import java.util.HashSet;
import java.util.Set;

public class TaskMemberDTO implements TaskDTO {
    private long id;
    private String name;
    private String description;
    private PublicVariables.TaskStatus taskStatus;
    private Set<SubtaskMemberDTO> subtasks = new HashSet<>();
    private Set<UserMemberDTO> users = new HashSet<>();

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
    }

    public TaskMemberDTO(){}

    public long getID() {return id;}
    public void setID(long id) {this.id = id;}

    public String getName(){return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public PublicVariables.TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(PublicVariables.TaskStatus taskStatus){this.taskStatus = taskStatus;}

    public Set<SubtaskMemberDTO> getSubtasks(){return subtasks;}
    public void setSubtasks(Set<SubtaskMemberDTO> subtasks){this.subtasks = subtasks;}

    public Set<UserMemberDTO> getUsers(){return users;}
    public void setUsers(Set<UserMemberDTO> users){this.users = users;}





}
