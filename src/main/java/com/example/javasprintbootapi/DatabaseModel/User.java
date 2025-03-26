package com.example.javasprintbootapi.DatabaseModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String login;
    private String password;

    private String name;
    private String lastName;

    @ManyToMany
    @JsonManagedReference
    private Set<Task> tasks;

    @ManyToMany
    @JsonBackReference
    private Set<Team> teams;


    public long getID() {
        return id;
    }

    public String getLogin(){
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Task> getTasks() { return tasks; }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Team> getTeams(){ return this.teams; }

    public void addToTeam(Team team){ this.teams.add(team); }


}


