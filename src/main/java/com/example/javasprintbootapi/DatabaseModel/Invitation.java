package com.example.javasprintbootapi.DatabaseModel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private long id;
    @ManyToOne
    @JsonBackReference
    private Team team;
    private boolean used = false;
}
