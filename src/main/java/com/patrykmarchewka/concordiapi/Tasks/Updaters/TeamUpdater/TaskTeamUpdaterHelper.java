package com.patrykmarchewka.concordiapi.Tasks.Updaters.TeamUpdater;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Teams.TeamService;

import java.util.function.Supplier;

public class TaskTeamUpdaterHelper {
    private final TeamService teamService;

    public TaskTeamUpdaterHelper(TeamService teamService){
        this.teamService = teamService;
    }

    void sharedUpdate(Task task, TaskRequestBody body){
        task.setTeam(teamService.getTeamByID(body.getTeam()));
        teamService.addTask(teamService.getTeamByID(body.getTeam()),task);
    }

    void removeTeamIfAssigned(Task task, Supplier<Team> team){
        if (team.get().getTasks().contains(task)){
            teamService.removeTaskFromTeam(team.get(),task);
        }
    }
}
