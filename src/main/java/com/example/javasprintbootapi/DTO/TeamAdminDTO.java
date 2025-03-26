package com.example.javasprintbootapi.DTO;

import com.example.javasprintbootapi.DatabaseModel.Task;
import com.example.javasprintbootapi.DatabaseModel.Team;
import com.example.javasprintbootapi.DatabaseModel.User;
import com.example.javasprintbootapi.PublicVariables;
import com.example.javasprintbootapi.TeamUserRoleService;

import java.util.Set;

public class TeamAdminDTO {

    private long id;
    private String name;
    private Set<User> teammates;
    private Set<Task> tasks;
    private Set<User> admins;
    private Set<User> managers;
    private Set<User> members;

    public TeamAdminDTO(Team team, TeamUserRoleService service){
        this.id = team.getId();
        this.name = team.getName();
        this.teammates = team.getTeammates();
        this.tasks = team.getTasks();
        this.admins = service.getAllRole(team, PublicVariables.UserRole.ADMIN);

    }
}
