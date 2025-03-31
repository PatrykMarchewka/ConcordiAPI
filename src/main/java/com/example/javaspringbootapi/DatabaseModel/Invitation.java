package com.example.javaspringbootapi.DatabaseModel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "Invitations")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String UUID;
    @ManyToOne
    @JsonBackReference
    private Team team;
    private boolean used = false;
}
