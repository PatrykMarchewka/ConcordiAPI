package com.example.javasprintbootapi.DatabaseModel;

import com.example.javasprintbootapi.PublicVariables;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "Subtasks")
public class Subtask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @OneToOne
    private Task task;

    @Enumerated(value = EnumType.STRING)
    private PublicVariables.TaskStatus taskStatus;


    public long getID() {
        return ID;
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
