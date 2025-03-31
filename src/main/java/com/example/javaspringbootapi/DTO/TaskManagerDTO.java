package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Subtask;
import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.User;
import com.example.javaspringbootapi.PublicVariables;

import java.util.Date;
import java.util.Set;

public class TaskManagerDTO {
    private String name;
    private String description;
    private PublicVariables.TaskStatus taskStatus;
    private Set<SubtaskMemberDTO> subtasks;
    private Set<UserMemberDTO> users;
    private Date creationDate;
    private Date updateDate;

    public TaskManagerDTO(Task task){
        this.name = task.getName();
        this.description = task.getDescription();
        this.taskStatus = task.getTaskStatus();
        this.creationDate = task.getCreationDate();
        this.updateDate = task.getUpdateDate();
        for (User user : task.getUsers()){
            users.add(new UserMemberDTO(user));
        }
        for (Subtask subtask : task.getSubtasks()){
            subtasks.add(new SubtaskMemberDTO(subtask));
        }
    }
}
