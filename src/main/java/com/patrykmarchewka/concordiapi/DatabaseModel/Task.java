package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.patrykmarchewka.concordiapi.TaskStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus taskStatus;
    @Column(nullable = false)
    private OffsetDateTime creationDate;
    private OffsetDateTime updateDate;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "task")
    @Column(nullable = false)
    @JsonManagedReference
    private Set<Subtask> subtasks = new HashSet<>();

    @ManyToMany(mappedBy = "userTasks")
    @JsonBackReference
    private Set<User> users = new HashSet<>();

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "team_id", nullable = false)
    private Team assignedTeam;

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

    public Team getAssignedTeam(){ return this.assignedTeam; }
    public void setAssignedTeam(Team assignedTeam){ this.assignedTeam = assignedTeam; }

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
