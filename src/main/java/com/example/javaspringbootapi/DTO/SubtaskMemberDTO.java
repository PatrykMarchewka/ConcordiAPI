package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Subtask;
import com.example.javaspringbootapi.PublicVariables;

public class SubtaskMemberDTO {
    private String name;
    private String description;
    private PublicVariables.TaskStatus taskStatus;


    public SubtaskMemberDTO(Subtask subtask){
        this.name = subtask.getName();
        this.description = subtask.getDescription();
        this.taskStatus = subtask.getTaskStatus();
    }
}
