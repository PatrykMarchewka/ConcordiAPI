package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;

public interface TeamPATCHUpdater extends TeamUpdater{
    void PATCHUpdate(Team team, TeamRequestBody body);
}
