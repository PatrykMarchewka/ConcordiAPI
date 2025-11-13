package com.patrykmarchewka.concordiapi.DTO.TeamDTO;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamIdentity;

import java.util.Objects;
import java.util.Set;

public interface TeamDTO extends TeamIdentity {

    void setID(long id);
    void setName(String name);
    void setTasks(Set<TaskMemberDTO> tasks);

    default boolean equalsTeam(TeamIdentity team){
        return Objects.equals(this.getID(), team.getID()) &&
                Objects.equals(this.getName(), team.getName());
    }
}