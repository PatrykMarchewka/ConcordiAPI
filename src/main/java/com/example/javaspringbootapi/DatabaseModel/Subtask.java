package com.example.javaspringbootapi.DatabaseModel;

import com.example.javaspringbootapi.PublicVariables;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Subtasks")
public class Subtask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @ManyToOne
    @JsonBackReference
    private Task task;

    @Enumerated(value = EnumType.STRING)
    private PublicVariables.TaskStatus taskStatus;


    public long getID() {
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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        Subtask subtask = (Subtask) o;
        return id != null && id.equals(subtask.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
