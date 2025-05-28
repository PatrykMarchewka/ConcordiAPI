package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;

public interface TeamPUTUpdater extends TeamUpdater{
    void PUTUpdate(Team team, TeamRequestBody body);
}
