package com.example.javasprintbootapi.DatabaseModel;

import com.example.javasprintbootapi.PublicVariables;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String login;
    private String password;

    private String name;
    private String lastName;

    @Enumerated(value = EnumType.STRING)
    private PublicVariables.UserStatus status;

    @ManyToMany
    private Set<Task> tasks;

    @OneToMany
    private Set<Task> ownership;


    public long getID() {
        return id;
    }

    public String getLogin(){
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

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

    public PublicVariables.UserStatus getStatus() {
        return status;
    }

    public void setStatus(PublicVariables.UserStatus status) {
        this.status = status;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Task> getOwnership() {
        return ownership;
    }

    public void setOwnership(Set<Task> ownership) {
        this.ownership = ownership;
    }


}


