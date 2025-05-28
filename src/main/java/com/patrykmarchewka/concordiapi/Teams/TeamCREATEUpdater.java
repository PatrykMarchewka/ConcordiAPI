package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;

public interface TeamCREATEUpdater extends TeamUpdater{
    void CREATEUpdate(Team team, TeamRequestBody body);
}
