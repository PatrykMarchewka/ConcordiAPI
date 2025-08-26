package com.patrykmarchewka.concordiapi.DTO.TeamDTO;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.UserRole;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamManagerDTO implements TeamDTO {
    private long id;
    private String name;
    private Set<TaskManagerDTO> tasks = new HashSet<>();
    private Map<UserRole, Set<UserMemberDTO>> usersByRole = new EnumMap<>(UserRole.class);

    public TeamManagerDTO(Team team, TeamUserRoleService service){
        this.id = team.getId();
        this.name = team.getName();
        for (Task task : team.getTeamTasks()){
            tasks.add(new TaskManagerDTO(task));
        }

        for (UserRole role : UserRole.values()){
            if (role.isAdmin() || role.isBanned()){
                usersByRole.put(role, new HashSet<>());
            }
            Set<UserMemberDTO> set = service.getAllByTeamAndUserRole(team,role).stream().map(UserMemberDTO::new).collect(Collectors.toUnmodifiableSet());
            usersByRole.put(role,set);
        }
    }

    public TeamManagerDTO(){}

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public Set<TaskManagerDTO> getTasks(){return tasks;}
    public void setTasks(Set<TaskManagerDTO> tasks){this.tasks = tasks;}

    public Map<UserRole, Set<UserMemberDTO>> getUsersByRole(){ return this.usersByRole; }
    public void setUsersByRole(Map<UserRole, Set<UserMemberDTO>> usersByRole) { this.usersByRole = usersByRole; }
}
