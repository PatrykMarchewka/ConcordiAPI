package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;

public interface SubtaskPUTUpdaterWithTeam extends SubtaskPUTUpdater{
    void setTeam(Team team);
}
