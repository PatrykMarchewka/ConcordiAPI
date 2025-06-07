package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ManyToMany
    @JsonManagedReference
    private Set<User> teammates = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Invitation> invitations = new HashSet<>();

    public long getId(){ return this.id; }

    public String getName(){ return this.name; }

    public void setName(String name){ this.name = name; }

    public Set<User> getTeammates(){ return this.teammates; }
    public void addTeammate(User user){this.teammates.add(user);}
    public void removeTeammate(User user){this.teammates.remove(user);}
    public boolean checkTeammate(User user){ return this.teammates.contains(user); }
    public void setTeammates(Set<User> teammates){this.teammates = teammates;}

    public Set<Task> getTasks() { return tasks; }
    public void addTask(Task task){ this.tasks.add(task); }
    public void removeTask(Task task){ this.tasks.remove(task); }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Invitation> getInvitations(){ return this.invitations; }
    public void setInvitations(Set<Invitation> invitations){this.invitations = invitations;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        Team team = (Team) o;
        return id != null && id.equals(team.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
