package com.example.javaspringbootapi.DatabaseModel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "Teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    @ManyToMany
    @JsonManagedReference
    private Set<User> teammates;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Task> tasks;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Invitation> invitations;

    public long getId(){ return this.id; }

    public String getName(){ return this.name; }

    public void setName(String name){ this.name = name; }

    public Set<User> getTeammates(){ return this.teammates; }

    public void addTeammate(User user) { this.teammates.add(user); }

    public Set<Task> getTasks() { return tasks; }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Invitation> getInvitations(){ return this.invitations; }

    public void addInvitation(Invitation invitation){ this.invitations.add(invitation); }



}
