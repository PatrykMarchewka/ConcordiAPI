package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;

import java.util.Set;

public class UserMemberDTO {
    private long id;
    private String name;
    private String lastName;


    public UserMemberDTO(User user){
        this.id = user.getID();
        this.name = user.getName();
        this.lastName = user.getLastName();
    }

    @Override
    public String toString(){
        return id + ":" + name + " " + lastName;
    }
}
