package com.example.javasprintbootapi.DatabaseModel;

import com.example.javasprintbootapi.PublicVariables;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    private String Login;
    private String Password;

    private String Name;
    private String LastName;

    @Enumerated(value = EnumType.STRING)
    private PublicVariables.UserStatus Status;

    @ManyToMany
    private Set<Task> tasks;

    @OneToMany
    private Set<Task> ownership;


    public long getID() {
        return ID;
    }

    public String getLogin(){
        return this.Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public PublicVariables.UserStatus getStatus() {
        return Status;
    }

    public void setStatus(PublicVariables.UserStatus status) {
        Status = status;
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


