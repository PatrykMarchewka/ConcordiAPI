package com.example.javasprintbootapi.DatabaseModel;

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

    private String Name;
    @ManyToMany
    @JsonManagedReference
    private Set<User> teammates;

    @OneToMany
    @JsonManagedReference
    private Set<Task> tasks;

    @OneToMany
    @JsonManagedReference
    private Set<Invitation> invitations;

    public long getId(){ return this.id; }

    public String getName(){ return this.Name; }

    public void setName(String name){ this.Name = name; }

    public Set<User> getTeammates(){ return this.teammates; }

    public void addTeammate(User user) { this.teammates.add(user); }

    public Set<Task> getTasks() { return tasks; }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Invitation> getInvitations(){ return this.invitations; }

    public void addInvitation(Invitation invitation){ this.invitations.add(invitation); }



}
