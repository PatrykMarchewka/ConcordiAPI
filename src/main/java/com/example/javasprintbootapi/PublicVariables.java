package com.example.javasprintbootapi;

public class PublicVariables {

    public enum TaskStatus{
        NEW,
        CANCELLED,
        INPROGRESS,
        HALTED,
        FINISHED;
        public static TaskStatus fromString(String name){
            for (TaskStatus status : TaskStatus.values()){
                if (status.name().equalsIgnoreCase(name)){
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + name);

        }
    }

    public enum UserRole{
        ADMIN,
        MANAGER,
        MEMBER,
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
