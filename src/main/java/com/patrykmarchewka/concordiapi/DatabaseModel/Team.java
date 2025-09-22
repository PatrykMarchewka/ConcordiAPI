package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "Teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true, mappedBy = "team")
    @Column(nullable = false)
    private Set<TeamUserRole> userRoles = new HashSet<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true, mappedBy = "assignedTeam")
    @Column(nullable = false)
    private Set<Task> teamTasks = new HashSet<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true, mappedBy = "invitingTeam")
    @Column(nullable = false)
    private Set<Invitation> invitations = new HashSet<>();

    public long getID(){ return this.id; }

    public String getName(){ return this.name; }

    public void setName(String name){ this.name = name; }

    public Set<User> getTeammates(){ return this.getUserRoles().stream().map(TeamUserRole::getUser).collect(Collectors.toUnmodifiableSet()); }

    public Set<TeamUserRole> getUserRoles(){ return this.userRoles;}
    public void setUserRoles(Set<TeamUserRole> userRoles){this.userRoles = userRoles;}

    public Set<Task> getTeamTasks() { return teamTasks; }
    public void setTeamTasks(Set<Task> tasks) {
        this.teamTasks = tasks;
    }

    public Set<Invitation> getInvitations(){ return this.invitations; }
    public void setInvitations(Set<Invitation> invitations){this.invitations = invitations;}


    public TeamUserRole addUserRole(User user, UserRole role) {
        TeamUserRole tmr = new TeamUserRole(user, this, role);
        user.addTeamRole(tmr);
        this.userRoles.add(tmr);
        return tmr;
    }

    public Team removeUserRole(TeamUserRole role){
        role.getUser().removeTeamRole(role);
        this.userRoles.remove(role);
        return this;
    }

    public boolean checkUser(User user){return this.userRoles.stream().map(TeamUserRole::getUser).anyMatch(user::equals);}
    public boolean checkUser(long ID){ return this.userRoles.stream().anyMatch(ur -> ur.getUser().getID() == ID); }


    public void addTask(Task task){
        task.setAssignedTeam(this);
        this.teamTasks.add(task);
    }
    public void removeTask(Task task){
        this.teamTasks.remove(task);
    }






    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        Team team = (Team) o;
        return id != null && id.equals(team.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
