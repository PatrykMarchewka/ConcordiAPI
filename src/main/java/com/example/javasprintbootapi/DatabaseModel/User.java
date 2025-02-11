package com.example.javasprintbootapi.DatabaseModel;

import jakarta.persistence.*;

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
    private UserStatus Status;

    private enum UserStatus{
        ADMIN,
        EMPLOYEE,
        USER,
        BANNED
    }


    public User(){

    }
}
