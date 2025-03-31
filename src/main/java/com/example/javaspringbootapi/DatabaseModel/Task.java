package com.example.javaspringbootapi.DatabaseModel;
import com.example.javaspringbootapi.PublicVariables;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.lang.Nullable;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Subtask> subtasks;

    @ManyToMany
    @JsonBackReference
    private Set<User> users;

    @ManyToOne
    @JsonBackReference
    private Team team;

    public long getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PublicVariables.TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(PublicVariables.TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Set<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(Set<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public Set<User> getUsers() {
        return users;
    }
    public void setUsers(Set<User> users){
        this.users = users;
    }

    public Team getTeam(){ return this.team; }
    public void setTeam(Team team){ this.team = team; }
}
