package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;

import java.util.function.Supplier;

public class SubtaskTaskUpdater implements SubtaskCREATEUpdater,SubtaskPUTUpdater{

    private final TaskService taskService;
    private final TeamService teamService;
    private final Supplier<Long> teamID;

    public SubtaskTaskUpdater(TaskService taskService, TeamService teamService, Supplier<Long> teamID){
        this.taskService = taskService;
        this.teamService = teamService;
        this.teamID = teamID;
    }

    @Override
    public void CREATEUpdate(Subtask subtask, SubtaskRequestBody body) {
        sharedUpdate(subtask, body);
    }

    @Override
    public void PUTUpdate(Subtask subtask, SubtaskRequestBody body) {
        sharedUpdate(subtask, body);
    }

    void sharedUpdate(Subtask subtask, SubtaskRequestBody body){
        if (subtask.getTask() != null){
            taskService.removeSubtaskFromTask(subtask.getTask(), subtask);
        }

        subtask.setTask(taskService.getTaskByIDAndTeam(body.getTask(), teamService.getTeamByID(teamID.get())));
        taskService.addSubtaskToTask(taskService.getTaskByIDAndTeam(body.getTask(), teamService.getTeamByID(teamID.get())),subtask);
    }
}
