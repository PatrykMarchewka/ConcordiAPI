package com.patrykmarchewka.concordiapi.DTO.TeamDTO;

import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamIdentity;

public interface TeamDTO extends TeamIdentity {
    void setID(long id);
    void setName(String name);
}