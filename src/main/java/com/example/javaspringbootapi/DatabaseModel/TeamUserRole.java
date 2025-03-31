package com.example.javaspringbootapi.DatabaseModel;

import com.example.javaspringbootapi.PublicVariables;
import jakarta.persistence.*;

@Entity
public class TeamUserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Team team;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private PublicVariables.UserRole userRole;


    public Team getTeam(){ return this.team; }

    public void setTeam(Team team) { this.team = team; }

    public User getUser(){ return this.user; }

    public void setUser(User user) { this.user = user; }

    public PublicVariables.UserRole getUserRole(){ return this.userRole; }

    public void setUserRole(PublicVariables.UserRole role){ this.userRole = role;}
}
