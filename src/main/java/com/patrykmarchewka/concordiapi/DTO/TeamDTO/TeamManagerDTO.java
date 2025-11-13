package com.patrykmarchewka.concordiapi.DTO.TeamDTO;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRolesAndTasks;
import com.patrykmarchewka.concordiapi.UserRole;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamManagerDTO implements TeamDTO {
    private long id;
    private String name;
    private Set<TaskMemberDTO> tasks = new HashSet<>();
    private Map<UserRole, Set<UserMemberDTO>> usersByRole = new EnumMap<>(UserRole.class);

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

    @Override
    public long getID() {return id;}
    @Override
    public void setID(long id){this.id = id;}

    @Override
    public String getName(){return name;}
    @Override
    public void setName(String name){this.name = name;}

    public Set<TaskMemberDTO> getTasks(){return tasks;}
    @Override
    public void setTasks(Set<TaskMemberDTO> tasks){this.tasks = tasks;}

    public Map<UserRole, Set<UserMemberDTO>> getUsersByRole(){ return this.usersByRole; }
    public void setUsersByRole(Map<UserRole, Set<UserMemberDTO>> usersByRole) { this.usersByRole = usersByRole; }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof TeamManagerDTO teamManagerDTO)) return false;
        return Objects.equals(id, teamManagerDTO.getID()) &&
                Objects.equals(name, teamManagerDTO.getName()) &&
                Objects.equals(tasks, teamManagerDTO.getTasks()) &&
                Objects.equals(usersByRole, teamManagerDTO.getUsersByRole());

    }

    @Override
    public int hashCode(){
        return Objects.hash(id, name);
    }
}
