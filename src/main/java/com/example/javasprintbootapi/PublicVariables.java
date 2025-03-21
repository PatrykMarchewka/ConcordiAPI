package com.example.javasprintbootapi;

public class PublicVariables {

    public static UserRole loggedUserRole;

    public enum TaskStatus{
        NEW,
        CANCELLED,
        INPROGRESS,
        HALTED,
        FINISHED
    }

    public enum UserRole{
        ADMIN,
        EMPLOYEE,
        USER,
        BANNED;

        public static UserRole fromString(String name){
            for (UserRole role : UserRole.values() ){
                if (role.name().equalsIgnoreCase(name)){
                    return role;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + name);
        }



    }







}
