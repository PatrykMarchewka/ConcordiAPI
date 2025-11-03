package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserFull;
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
@Table(name = "Users")
public class User implements UserFull {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String lastName;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "assignedUser")
    @Column(nullable = false)
    private Set<UserTask> userTasks = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "user")
    @Column(nullable = false)
    private Set<TeamUserRole> teamRoles = new HashSet<>();


    @Override
    public long getID() {
        return id;
    }

    @Override
    public String getLogin(){ return this.login; }
    public void setLogin(String login) { this.login = login; }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }


    @Override
    public Set<UserTask> getUserTasks() { return userTasks; }
    public void addUserTask(UserTask task){ this.userTasks.add(task); }
    public void removeUserTask(UserTask task){ this.userTasks.remove(task); }
    public void setUserTasks(Set<UserTask> tasks) {this.userTasks = tasks;}

    @Override
    public Set<Team> getTeams(){return this.teamRoles.stream().map(TeamUserRole::getTeam).collect(Collectors.toUnmodifiableSet());}

    @Override
    public Set<TeamUserRole> getTeamRoles() { return this.teamRoles; }
    public void addTeamRole(TeamUserRole tmr){this.teamRoles.add(tmr);}
    public void removeTeamRole(TeamUserRole tmr){this.teamRoles.remove(tmr);}
    public void setTeamRoles(Set<TeamUserRole> teamRoles) { this.teamRoles = teamRoles; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != null && login != null && id.equals(user.getID()) && login.equals(user.getLogin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login);
    }







}


