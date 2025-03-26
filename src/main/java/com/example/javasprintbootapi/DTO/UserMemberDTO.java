package com.example.javasprintbootapi.DTO;

import com.example.javasprintbootapi.DatabaseModel.Task;
import com.example.javasprintbootapi.DatabaseModel.Team;
import com.example.javasprintbootapi.DatabaseModel.User;

import java.util.Set;

public class UserMemberDTO {
    private String name;
    private String lastName;


    public UserMemberDTO(User user){
        this.name = user.getName();
        this.lastName = user.getLastName();
    }
}
