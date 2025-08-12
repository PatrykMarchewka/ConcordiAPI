package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

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
    @JoinTable(
            name = "users_tasks",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "task_id", nullable = false)
    )
    private Set<Task> userTasks = new HashSet<>();

    @ManyToMany(mappedBy = "teammates")
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

    public Set<Task> getUserTasks() { return userTasks; }
    public void addTask(Task task){ this.userTasks.add(task); }
    public void removeTask(Task task){ this.userTasks.remove(task); }
    public void setUserTasks(Set<Task> tasks) {this.userTasks = tasks;}

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


