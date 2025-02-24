package com.example.javasprintbootapi;

public class PublicVariables {

    public static UserStatus loggedUserRole;

    public enum TaskStatus{
        NEW,
        CANCELLED,
        INPROGRESS,
        HALTED,
        FINISHED
    }

    public enum UserStatus{
        ADMIN,
        EMPLOYEE,
        USER,
        BANNED
    }





}
