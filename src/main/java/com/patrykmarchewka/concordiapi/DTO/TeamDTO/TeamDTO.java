package com.patrykmarchewka.concordiapi.DTO.TeamDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;

import java.util.Objects;

public interface TeamDTO {
    long getID();
    String getName();

    default boolean equalsTeam(Team team){
        return Objects.equals(this.getID(), team.getID()) &&
                Objects.equals(this.getName(), team.getName());
    }
}
