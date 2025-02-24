package com.example.javasprintbootapi.DatabaseModel;

import com.example.javasprintbootapi.PublicVariables;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    private String Login;
    private String Password;

    private String Name;
    private String LastName;

    @Enumerated(value = EnumType.STRING)
    private PublicVariables.UserStatus Status;




    public User(){

    }
}


