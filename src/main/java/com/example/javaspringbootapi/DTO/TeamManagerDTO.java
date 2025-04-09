package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;
import com.example.javaspringbootapi.PublicVariables;
import com.example.javaspringbootapi.TeamUserRoleService;
import java.util.HashSet;
import java.util.Set;

public class TeamManagerDTO {
    private long id;
    private String name;
    private Set<UserMemberDTO> teammates = new HashSet<>();
    private Set<TaskManagerDTO> tasks = new HashSet<>();
    private Set<UserMemberDTO> managers = new HashSet<>();

    public TeamManagerDTO(Team team, TeamUserRoleService service){
        this.id = team.getId();
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

    public TeamManagerDTO(){}

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public Set<UserMemberDTO> getTeammates(){return teammates;}
    public void setTeammates(Set<UserMemberDTO> teammates){this.teammates = teammates;}

    public Set<TaskManagerDTO> getTasks(){return tasks;}
    public void setTasks(Set<TaskManagerDTO> tasks){this.tasks = tasks;}

    public Set<UserMemberDTO> getManagers(){return managers;}
    public void setManagers(Set<UserMemberDTO> managers){this.managers = managers;}
}
