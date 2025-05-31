package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Teams.TeamService;

public class TaskTeamUpdater implements TaskCREATEUpdater,TaskPUTUpdater{

    private final TeamService teamService;

    public TaskTeamUpdater(TeamService teamService){
        this.teamService = teamService;
    }

    @Override
    public void CREATEUpdate(Task task, TaskRequestBody body) {
        sharedUpdate(task,body,teamService);
        teamService.addTask(teamService.getTeamByID(body.getTeam()), task);
    }

    @Override
    public void PUTUpdate(Task task, TaskRequestBody body) {
        sharedUpdate(task,body,teamService);
    }

    void sharedUpdate(Task task, TaskRequestBody body, TeamService teamService){
        task.setTeam(teamService.getTeamByID(body.getTeam()));
    }
}
