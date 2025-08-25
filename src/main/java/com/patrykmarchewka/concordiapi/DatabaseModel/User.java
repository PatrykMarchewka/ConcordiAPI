package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "user")
    @JsonManagedReference
    private Set<TeamUserRole> teamRoles = new HashSet<>();


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

    public Set<Team> getTeams(){return this.teamRoles.stream().map(TeamUserRole::getTeam).collect(Collectors.toUnmodifiableSet());}

    public Set<TeamUserRole> getTeamRoles() { return this.teamRoles; }
    public void addTeamRole(TeamUserRole tmr){this.teamRoles.add(tmr);}
    public void removeTeamRole(TeamUserRole tmr){this.teamRoles.remove(tmr);}
    public void setTeamRoles(Set<TeamUserRole> teamRoles) { this.teamRoles = teamRoles; }

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


