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
}
