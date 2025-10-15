package com.patrykmarchewka.concordiapi.DTO.TeamDTO;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamIdentity;

import java.util.Objects;

public interface TeamDTO extends TeamIdentity {

    //Legacy, scheduled for removal
    default boolean equalsTeam(Team team){
        return Objects.equals(this.getID(), team.getID()) &&
                Objects.equals(this.getName(), team.getName());
    }


    default boolean equalsTeam(TeamIdentity team){
        return Objects.equals(this.getID(), team.getID()) &&
                Objects.equals(this.getName(), team.getName());
    }
}