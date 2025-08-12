package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
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

@Entity
@Table(name = "Teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @ManyToMany
    @JsonManagedReference
    @JoinTable(
            name = "teams_users",
            joinColumns = @JoinColumn(name = "team_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false)
    )
    private Set<User> teammates = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "assignedTeam")
    @JsonManagedReference
    private Set<Task> teamTasks = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "invitingTeam")
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

    public Set<Task> getTeamTasks() { return teamTasks; }
    public void addTask(Task task){ this.teamTasks.add(task); }
    public void removeTask(Task task){ this.teamTasks.remove(task); }

    public void setTeamTasks(Set<Task> tasks) {
        this.teamTasks = tasks;
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
