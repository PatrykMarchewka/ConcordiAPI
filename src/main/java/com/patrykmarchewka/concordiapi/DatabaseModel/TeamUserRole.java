package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class TeamUserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

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
        if (!(o instanceof TeamUserRole)) return false;
        TeamUserRole role = (TeamUserRole) o;
        return id != null && id.equals(role.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
