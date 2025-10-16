package com.patrykmarchewka.concordiapi.DTO.TeamDTO;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRoles;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRolesAndTasks;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamMemberDTO implements TeamDTO {
    private long id;
    private String name;
    private int teammateCount;
    private Set<TaskMemberDTO> tasks = new HashSet<>();
    private Set<UserMemberDTO> owners = new HashSet<>();

    /**
     * @deprecated Will be replaced by {@link #TeamMemberDTO(TeamWithUserRolesAndTasks, User)}
     * @param team
     * @param user
     */
    public TeamMemberDTO(Team team, User user) {
        this.id = team.getID();
        this.name = team.getName();
        this.teammateCount = team.getUserRoles().size();
        if (user != null){
            this.tasks = team.getTeamTasks().stream().filter(t -> t.getUsers().contains(user)).map(TaskMemberDTO::new).collect(Collectors.toUnmodifiableSet());
        }

        this.owners = team.getUserRoles().stream().filter(ur -> ur.getUserRole().isOwner()).map(ur -> new UserMemberDTO(ur.getUser())).collect(Collectors.toUnmodifiableSet());
    }

    public TeamMemberDTO(TeamWithUserRolesAndTasks team, User user){
        this.id = team.getID();
        this.name = team.getName();
        this.teammateCount = team.getUserRoles().size();
        this.tasks = team.getTeamTasks().stream().filter(t -> t.getUsers().contains(user)).map(TaskMemberDTO::new).collect(Collectors.toUnmodifiableSet());
        this.owners = team.getUserRoles().stream().filter(ur -> ur.getUserRole().isOwner()).map(ur -> new UserMemberDTO(ur.getUser())).collect(Collectors.toUnmodifiableSet());
    }

    public TeamMemberDTO(TeamWithUserRoles team) {
        this.id = team.getID();
        this.name = team.getName();
        this.teammateCount = team.getUserRoles().size();
        this.owners = team.getUserRoles().stream().filter(ur -> ur.getUserRole().isOwner()).map(ur -> new UserMemberDTO(ur.getUser())).collect(Collectors.toUnmodifiableSet());
    }

    public TeamMemberDTO(){}

    public long getID() {return id;}
    public void setID(long id) {this.id = id;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public int getTeammateCount(){return teammateCount;}
    public void setTeammateCount(int teammateCount){this.teammateCount = teammateCount;}

    public Set<TaskMemberDTO> getTasks(){return tasks;}
    public void setTasks(Set<TaskMemberDTO> tasks){this.tasks = tasks;}

    public Set<UserMemberDTO> getOwners(){return owners;}
    public void setOwners(Set<UserMemberDTO> owners){this.owners = owners;}


    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof TeamMemberDTO teamMemberDTO)) return false;
        return Objects.equals(id, teamMemberDTO.getID()) &&
                Objects.equals(name, teamMemberDTO.getName()) &&
                Objects.equals(teammateCount, teamMemberDTO.getTeammateCount()) &&
                Objects.equals(tasks, teamMemberDTO.getTasks()) &&
                Objects.equals(owners, teamMemberDTO.getOwners());

    }

    @Override
    public int hashCode(){
        return Objects.hash(id, name, teammateCount);
    }
}
