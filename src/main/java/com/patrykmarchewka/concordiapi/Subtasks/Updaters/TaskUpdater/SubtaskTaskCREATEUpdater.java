package com.patrykmarchewka.concordiapi.Subtasks.Updaters.TaskUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskCREATEUpdaterWithTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubtaskTaskCREATEUpdater implements SubtaskCREATEUpdaterWithTeam {


    private Team team;
    private final SubtaskTaskUpdaterHelper subtaskTaskUpdaterHelper;

    @Autowired
    public SubtaskTaskCREATEUpdater(SubtaskTaskUpdaterHelper subtaskTaskUpdaterHelper) {
        this.subtaskTaskUpdaterHelper = subtaskTaskUpdaterHelper;
    }

    @Override
    public void CREATEUpdate(Subtask subtask, SubtaskRequestBody body) {
        if (this.team == null){
            throw new BadRequestException("The team is set to null");
        }
        subtaskTaskUpdaterHelper.sharedUpdate(subtask,body,team);
    }

    @Override
    public void setTeam(Team team) {
        this.team = team;
    }
}
