package com.example.javaspringbootapi.DatabaseModel;

import com.example.javaspringbootapi.PublicVariables;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class TeamUserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Team team;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private PublicVariables.UserRole userRole;

    public Long getID() {return id;}
    public void setId(Long id) {this.id = id;}

    public Team getTeam(){ return this.team; }

    public void setTeam(Team team) { this.team = team; }

    public User getUser(){ return this.user; }

    public void setUser(User user) { this.user = user; }

    public PublicVariables.UserRole getUserRole(){ return this.userRole; }

    public void setUserRole(PublicVariables.UserRole role){ this.userRole = role;}


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
