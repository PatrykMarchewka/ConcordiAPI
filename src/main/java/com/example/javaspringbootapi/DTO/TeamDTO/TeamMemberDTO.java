package com.example.javaspringbootapi.DTO.TeamDTO;

import com.example.javaspringbootapi.DTO.TaskDTO.TaskMemberDTO;
import com.example.javaspringbootapi.DTO.UserDTO.UserMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;
import com.example.javaspringbootapi.PublicVariables;
import com.example.javaspringbootapi.TeamUserRoleService;

import java.util.HashSet;
import java.util.Set;

public class TeamMemberDTO {
    private long id;
    private String name;
    private int teammateCount;
    private Set<TaskMemberDTO> tasks = new HashSet<>();
    private Set<UserMemberDTO> owners = new HashSet<>();

    public TeamMemberDTO(Team team, User user, TeamUserRoleService service){
        this.id = team.getId();
        this.name = team.getName();
        this.teammateCount = team.getTeammates().size();
        if (user != null){
            Set<TaskMemberDTO> filteredTasks = new HashSet<>();
            for (Task task : team.getTasks()){
                if (task.getUsers().contains(user)){
                    filteredTasks.add(new TaskMemberDTO(task));
                }
            }
            this.tasks = filteredTasks;
        }
        for (User user1 : service.getAllRole(team, PublicVariables.UserRole.OWNER)){
            this.owners.add(new UserMemberDTO(user1));
        }
    }

    public TeamMemberDTO(){}

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public int getTeammateCount(){return teammateCount;}
    public void setTeammateCount(int teammateCount){this.teammateCount = teammateCount;}

    public Set<TaskMemberDTO> getTasks(){return tasks;}
    public void setTasks(Set<TaskMemberDTO> tasks){this.tasks = tasks;}

    public Set<UserMemberDTO> getOwners(){return owners;}
    public void setOwners(Set<UserMemberDTO> owners){this.owners = owners;}
}
