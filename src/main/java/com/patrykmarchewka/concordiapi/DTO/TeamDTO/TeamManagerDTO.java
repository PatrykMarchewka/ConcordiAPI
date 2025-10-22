package com.patrykmarchewka.concordiapi.DTO.TeamDTO;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRolesAndTasks;
import com.patrykmarchewka.concordiapi.UserRole;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamManagerDTO implements TeamDTO {
    private long id;
    private String name;
    private Set<TaskMemberDTO> tasks = new HashSet<>();
    private Map<UserRole, Set<UserMemberDTO>> usersByRole = new EnumMap<>(UserRole.class);

    /**
     * @deprecated Will be replaced by {@link #TeamManagerDTO(TeamWithUserRolesAndTasks)}
     * @param team
     */
    @Deprecated
    public TeamManagerDTO(Team team){
        this.id = team.getID();
        this.name = team.getName();
        for (Task task : team.getTeamTasks()){
            tasks.add(new TaskMemberDTO(task));
        }

        for (UserRole role : UserRole.values()){
            Set<UserMemberDTO> set = (role.isAdmin() || role.isBanned()) ? Set.of() : team.getUserRoles().stream().filter(ur -> ur.getUserRole().equals(role)).map(ur -> new UserMemberDTO(ur.getUser())).collect(Collectors.toUnmodifiableSet());
            usersByRole.put(role, set);
        }
    }

    public TeamManagerDTO(TeamWithUserRolesAndTasks team){
        this.id = team.getID();
        this.name = team.getName();
        for (Task task : team.getTeamTasks()){
            tasks.add(new TaskMemberDTO(task));
        }

        for (UserRole role : UserRole.values()){
            Set<UserMemberDTO> set = (role.isAdmin() || role.isBanned()) ? Set.of() : team.getUserRoles().stream().filter(ur -> ur.getUserRole().equals(role)).map(ur -> new UserMemberDTO(ur.getUser())).collect(Collectors.toUnmodifiableSet());
            usersByRole.put(role, set);
        }
    }

    public TeamManagerDTO(){}

    public long getID() {return id;}
    public void setID(long id) {this.id = id;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public Set<TaskMemberDTO> getTasks(){return tasks;}
    public void setTasks(Set<TaskMemberDTO> tasks){this.tasks = tasks;}

    public Map<UserRole, Set<UserMemberDTO>> getUsersByRole(){ return this.usersByRole; }
    public void setUsersByRole(Map<UserRole, Set<UserMemberDTO>> usersByRole) { this.usersByRole = usersByRole; }
}
