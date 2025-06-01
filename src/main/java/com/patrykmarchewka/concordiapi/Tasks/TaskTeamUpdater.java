package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Teams.TeamService;

import java.util.function.Supplier;

public class TaskTeamUpdater implements TaskCREATEUpdater,TaskPUTUpdater,TaskPATCHUpdater{

    private final Supplier<Team> team;
    private final TeamService teamService;

    public TaskTeamUpdater(Supplier<Team> team, TeamService teamService){
        this.teamService = teamService;
        this.team = team;
    }

    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        sharedUpdate(task,body,teamService);
    }

    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        if (team.get().getTasks().contains(task)){
            teamService.removeTaskFromTeam(team.get(),task);
        }

        sharedUpdate(task,body,teamService);
    }

    @Override
    public void PATCHUpdate(Task task, TaskRequestBody body){
        if (team.get().getTasks().contains(task)){
            teamService.removeTaskFromTeam(team.get(),task);
        }
        sharedUpdate(task,body,teamService);
    }

    void sharedUpdate(Task task, TaskRequestBody body, TeamService teamService){
        task.setTeam(teamService.getTeamByID(body.getTeam()));
        teamService.addTask(teamService.getTeamByID(body.getTeam()),task);
    }
}
