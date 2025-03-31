package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;
import com.example.javaspringbootapi.PublicVariables;
import com.example.javaspringbootapi.TeamUserRoleService;
import java.util.HashSet;
import java.util.Set;

public class TeamManagerDTO {

    private String name;
    private Set<UserMemberDTO> teammates;
    private Set<TaskManagerDTO> tasks;
    private Set<UserMemberDTO> managers;

    public TeamManagerDTO(Team team, TeamUserRoleService service){
        this.name = team.getName();
        for (User user : team.getTeammates()){
            teammates.add(new UserMemberDTO(user));
        }
        for (Task task : team.getTasks()){
            tasks.add(new TaskManagerDTO(task));
        }
        Set<User> users = service.getAllRole(team, PublicVariables.UserRole.MANAGER);
        for (User user : users){
            managers.add(new UserMemberDTO(user));
        }



    }
}
