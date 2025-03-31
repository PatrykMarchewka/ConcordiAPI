package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;
import com.example.javaspringbootapi.PublicVariables;
import com.example.javaspringbootapi.TeamUserRoleService;

import java.util.Set;

public class TeamAdminDTO {

    private long id;
    private String name;
    private Set<UserMemberDTO> teammates;
    private Set<TaskManagerDTO> tasks;
    private Set<UserMemberDTO> admins;
    private Set<UserMemberDTO> managers;

    public TeamAdminDTO(Team team, TeamUserRoleService service){
        this.id = team.getId();
        this.name = team.getName();
        for (User teammate : team.getTeammates()){
            teammates.add(new UserMemberDTO(teammate));
        }
        for (Task task : team.getTasks()){
            tasks.add(new TaskManagerDTO(task));
        }
        for (User admin : service.getAllRole(team, PublicVariables.UserRole.ADMIN)){
            admins.add(new UserMemberDTO(admin));
        }
        for (User manager : service.getAllRole(team, PublicVariables.UserRole.MANAGER)){
            managers.add(new UserMemberDTO(manager));
        }

    }
}
