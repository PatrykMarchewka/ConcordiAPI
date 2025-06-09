package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.patrykmarchewka.concordiapi.TaskStatus;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Tasks")
public class Task {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Enumerated(value = EnumType.STRING)
    private TaskStatus taskStatus;


    private OffsetDateTime creationDate;


    private OffsetDateTime updateDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Subtask> subtasks = new HashSet<>();

    @ManyToMany
    @JsonBackReference
    private Set<User> users = new HashSet<>();

    @ManyToOne
    @JsonBackReference
    private Team team;

    public long getID() {
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

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(OffsetDateTime creationDate) {this.creationDate = creationDate;}

    public OffsetDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(OffsetDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Set<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(Set<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask){subtasks.add(subtask);}
    public void removeSubtask(Subtask subtask){subtasks.remove(subtask);}

    public Set<User> getUsers() {return users;}
    public void setUsers(Set<User> users){
        this.users = users;
    }
    public void addUser(User user){ this.users.add(user); }
    public void removeUser(User user) { this.users.remove(user); }

    public Team getTeam(){ return this.team; }
    public void setTeam(Team team){ this.team = team; }

    public boolean hasUser(User user){return users.contains(user);}



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id != null && id.equals(task.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }









}
