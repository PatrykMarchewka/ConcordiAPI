package com.example.javasprintbootapi.DatabaseModel;
import com.example.javasprintbootapi.PublicVariables;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Tasks")
public class Task {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    @Enumerated(value = EnumType.STRING)
    private PublicVariables.TaskStatus taskStatus;


    @Temporal(TemporalType.DATE)
    private Date creationDate;

    @Temporal(TemporalType.DATE)
    private Date updateDate;

    @OneToMany
    private Set<Subtask> subtasks;

    @OneToMany
    private Set<User> user;
}
