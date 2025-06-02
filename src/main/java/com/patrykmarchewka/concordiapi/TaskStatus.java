package com.patrykmarchewka.concordiapi;

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
