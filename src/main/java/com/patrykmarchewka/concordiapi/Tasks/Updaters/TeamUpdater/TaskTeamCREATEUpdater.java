package com.patrykmarchewka.concordiapi.Tasks.Updaters.TeamUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskCREATEUpdaterBasicWithTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskTeamCREATEUpdater implements TaskCREATEUpdaterBasicWithTeam {

    private final TaskTeamUpdaterHelper taskTeamUpdaterHelper;
    private Team team;

    @Autowired
    public TaskTeamCREATEUpdater(TaskTeamUpdaterHelper taskTeamUpdaterHelper) {
        this.taskTeamUpdaterHelper = taskTeamUpdaterHelper;
    }

    @Override
    public void CREATEUpdate(Task task)
    {
        if (this.team == null){
            throw new BadRequestException("The team is set to null");
        }
        taskTeamUpdaterHelper.sharedUpdate(task, team);
    }

    @Override
    public void setTeam(Team team) {
        this.team = team;
    }
}
