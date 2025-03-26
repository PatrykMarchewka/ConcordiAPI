package com.example.javasprintbootapi.DTO;

import com.example.javasprintbootapi.DatabaseModel.Task;
import com.example.javasprintbootapi.DatabaseModel.User;
import com.example.javasprintbootapi.PublicVariables;

import java.util.Set;

public class TaskMemberDTO {
    private String name;
    private String description;
    private PublicVariables.TaskStatus taskStatus;
    //private Set<Subtask> subtasks; //TODO: SubtaskDTO
    private Set<UserMemberDTO> users;

    public TaskMemberDTO(Task task){
        this.name = task.getName();
        this.description = task.getDescription();
        this.taskStatus = task.getTaskStatus();
        for (User user : task.getUsers()){
            users.add(new UserMemberDTO(user));
        }


    }


}
