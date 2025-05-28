package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String login;
    private String password;

    private String name;
    private String lastName;

    @ManyToMany
    @JsonManagedReference
    private Set<Task> tasks = new HashSet<>();

    @ManyToMany
    @JsonBackReference
    private Set<Team> teams = new HashSet<>();


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

    public void setTasks(Set<Task> tasks) {this.tasks = tasks;}

    public Set<Team> getTeams(){ return this.teams; }
    public void addTeam(Team team){this.teams.add(team);}
    public void removeTeam(Team team){this.teams.remove(team);}
    public boolean checkTeam(Team team){return this.teams.contains(team);}
    public void setTeams(Set<Team> teams){this.teams = teams;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id != null && id.equals(user.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }







}


