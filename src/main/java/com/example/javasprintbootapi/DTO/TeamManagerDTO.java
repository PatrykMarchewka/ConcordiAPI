package com.example.javasprintbootapi.DTO;

import com.example.javasprintbootapi.DatabaseModel.Task;
import com.example.javasprintbootapi.DatabaseModel.Team;
import com.example.javasprintbootapi.DatabaseModel.User;

import java.util.Set;

public class TeamManagerDTO {
    private String name;
    private Set<User> teammates;
    private Set<Task> tasks;
    private Set<User> managers;
    private Set<User> members;

    public TeamManagerDTO(Team team){
        this.name = team.getName();
        this.teammates = team.getTeammates();
        this.tasks = team.getTasks();
    }
}
