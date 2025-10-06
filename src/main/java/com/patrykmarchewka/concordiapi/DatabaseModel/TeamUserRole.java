package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Objects;

@Entity
@Table(name = "team_user_role", uniqueConstraints = @UniqueConstraint(columnNames = {"team_id", "user_id"}))
public class TeamUserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;


    public TeamUserRole(){}

    public TeamUserRole(User user, Team team, UserRole role){
        this.user = user;
        this.team = team;
        this.userRole = role;
    }

    public Long getID() {return id;}
    public void setId(Long id) {this.id = id;}

    public Team getTeam(){ return this.team; }

    public void setTeam(Team team) { this.team = team; }

    public User getUser(){ return this.user; }

    public void setUser(User user) { this.user = user; }

    public UserRole getUserRole(){ return this.userRole; }

    public void setUserRole(UserRole role){ this.userRole = role;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamUserRole role)) return false;
        return Objects.equals(team.getID(), role.getTeam().getID()) &&
                Objects.equals(user.getID(), role.getUser().getID()) &&
                Objects.equals(userRole, role.getUserRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(team.getID(), user.getID(), userRole);
    }
}
