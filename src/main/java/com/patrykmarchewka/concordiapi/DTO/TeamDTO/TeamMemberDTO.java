package com.patrykmarchewka.concordiapi.DTO.TeamDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRoles;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRolesAndTasks;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@JsonPropertyOrder({"ID", "Name", "User count", "Tasks", "Owners"})
public class TeamMemberDTO implements TeamDTO {
    private long id;
    private String name;
    private int teammateCount;
    private Set<TaskMemberDTO> tasks = new HashSet<>();
    private Set<UserMemberDTO> owners = new HashSet<>();

    public TeamMemberDTO(TeamWithUserRolesAndTasks team, long userID){
        this.id = team.getID();
        this.name = team.getName();
        this.teammateCount = team.getUserRoles().size();
        this.tasks = team.getTeamTasks().stream().filter(t -> t.hasUser(userID)).map(TaskMemberDTO::new).collect(Collectors.toUnmodifiableSet());
        this.owners = team.getUserRoles().stream().filter(ur -> ur.getUserRole().isOwner()).map(ur -> new UserMemberDTO(ur.getUser())).collect(Collectors.toUnmodifiableSet());
    }

    public TeamMemberDTO(TeamWithUserRoles team) {
        this.id = team.getID();
        this.name = team.getName();
        this.teammateCount = team.getUserRoles().size();
        this.owners = team.getUserRoles().stream().filter(ur -> ur.getUserRole().isOwner()).map(ur -> new UserMemberDTO(ur.getUser())).collect(Collectors.toUnmodifiableSet());
    }

    public TeamMemberDTO(){}

    @Override
    @JsonProperty("ID")
    public long getID() {return id;}
    @Override
    public void setID(long id){this.id = id;}

    @Override
    @JsonProperty("Name")
    public String getName(){return name;}
    @Override
    public void setName(String name){this.name = name;}

    @JsonProperty("User count")
    public int getTeammateCount(){return teammateCount;}
    public void setTeammateCount(int teammateCount){this.teammateCount = teammateCount;}

    @JsonProperty("Tasks")
    public Set<TaskMemberDTO> getTasks(){return tasks;}
    public void setTasks(Set<TaskMemberDTO> tasks){this.tasks = tasks;}

    @JsonProperty("Owners")
    public Set<UserMemberDTO> getOwners(){return owners;}
    public void setOwners(Set<UserMemberDTO> owners){this.owners = owners;}


    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof TeamMemberDTO teamMemberDTO)) return false;
        return id == teamMemberDTO.id &&
                Objects.equals(name, teamMemberDTO.name) &&
                teammateCount == teamMemberDTO.teammateCount &&
                Objects.equals(tasks, teamMemberDTO.tasks) &&
                Objects.equals(owners, teamMemberDTO.owners);

    }

    @Override
    public int hashCode(){
        return Objects.hash(id, name, teammateCount, tasks, owners);
    }
}
