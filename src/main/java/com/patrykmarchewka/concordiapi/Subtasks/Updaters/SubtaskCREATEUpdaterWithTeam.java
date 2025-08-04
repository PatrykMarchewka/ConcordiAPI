package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;

public interface SubtaskCREATEUpdaterWithTeam extends SubtaskCREATEUpdater{
    void setTeam(Team team);
}
