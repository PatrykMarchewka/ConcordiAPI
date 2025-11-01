package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamFull;
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
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "Teams")
public class Team implements TeamFull {

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

    @Override
    //0L to support equals and hashCode in TeamUserRole.java
    public long getID(){ return this.id != null ? id : 0L; }

    @Override
    public String getName(){ return this.name; }
    public void setName(String name){ this.name = name; }

    @Override
    public Set<TeamUserRole> getUserRoles(){ return this.userRoles;}
    public boolean checkUser(long ID){ return this.userRoles.stream().anyMatch(ur -> ur.getUser().getID() == ID); }
    public Set<User> getTeammates(){ return this.userRoles.stream().map(TeamUserRole::getUser).collect(Collectors.toUnmodifiableSet()); }
    public void setUserRoles(Set<TeamUserRole> userRoles){this.userRoles = userRoles;}

    @Override
    public Set<Task> getTeamTasks() { return teamTasks; }
    public void setTeamTasks(Set<Task> tasks) {
        this.teamTasks = tasks;
    }

    @Override
    public Set<Invitation> getInvitations(){ return this.invitations; }
    public void setInvitations(Set<Invitation> invitations){this.invitations = invitations;}


    public TeamUserRole addUserRole(User userWithTeamRoles, UserRole role) {
        TeamUserRole tmr = new TeamUserRole(userWithTeamRoles, this, role);
        userWithTeamRoles.addTeamRole(tmr);
        this.userRoles.add(tmr);
        return tmr;
    }

    public Team removeUserRole(TeamUserRole role){
        role.getUser().removeTeamRole(role);
        this.userRoles.remove(role);

        role.setTeam(null);
        role.setUser(null);
        role.setUserRole(null);

        return this;
    }

    public Task addTask(Task task){
        task.setAssignedTeam(this);
        this.teamTasks.add(task);
        return task;
    }

    public Team removeTask(Task task){
        task.setAssignedTeam(null);
//        Iterates through entire set looking for the one to remove and after removing stops
//        faster than .removeIf() since it doesnt scan entire Set everytime but only until hit
//        Iterator because hibernate defaults to PersistentSet and .remove() doesnt count it as same item in Set
        Iterator<Task> iterator = this.teamTasks.iterator();
        while (iterator.hasNext()){
            Task t = iterator.next();
            if (t.getID() == task.getID()){
                iterator.remove();
                break;
            }
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team team)) return false;
        return id != null && id.equals(team.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
