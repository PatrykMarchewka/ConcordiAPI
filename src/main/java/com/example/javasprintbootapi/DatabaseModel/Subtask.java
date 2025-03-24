package com.example.javasprintbootapi.DatabaseModel;

import com.example.javasprintbootapi.PublicVariables;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "Subtasks")
public class Subtask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;

    @ManyToOne
    @JsonBackReference
    private Task task;

    @Enumerated(value = EnumType.STRING)
    private PublicVariables.TaskStatus taskStatus;


    public long getId() {
        return id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public PublicVariables.TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(PublicVariables.TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}
