package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;

public interface SubtaskPATCHUpdaterWithTeam extends SubtaskPATCHUpdater{
    void setTeam(Team team);
}
