package com.example.javaspringbootapi;

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
        OWNER,
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

        public boolean isOwner(){
            return this == OWNER;
        }

        public boolean isOwnerOrAdmin(){
            return this == OWNER || this == ADMIN;
        }

        public boolean isManager(){
            return this == MANAGER;
        }
        public boolean isMember(){
            return this == MEMBER;
        }
    }





}
