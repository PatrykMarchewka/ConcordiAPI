package com.patrykmarchewka.concordiapi.Tasks.Updaters.TeamUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class TaskTeamUpdaterHelper {
    private final TeamService teamService;

    @Autowired
    public TaskTeamUpdaterHelper(@Lazy TeamService teamService){
        this.teamService = teamService;
    }

    void sharedUpdate(Task task, Team team){
        task.setAssignedTeam(team);
        teamService.addTask(team,task);
    }
}
