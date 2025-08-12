package com.patrykmarchewka.concordiapi.Tasks.Updaters;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;

public interface TaskCREATEUpdaterBasicWithTeam extends TaskCREATEUpdaterBasic{
    void setTeam(Team team);
}
