package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Subtask;
import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.User;
import com.example.javaspringbootapi.PublicVariables;

import java.util.Set;

public class TaskMemberDTO {
    private String name;
    private String description;
    private PublicVariables.TaskStatus taskStatus;
    private Set<SubtaskMemberDTO> subtasks;
    private Set<UserMemberDTO> users;

    public TaskMemberDTO(Task task){
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


}
