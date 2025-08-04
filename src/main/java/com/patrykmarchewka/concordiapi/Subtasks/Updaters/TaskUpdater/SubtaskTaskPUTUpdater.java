package com.patrykmarchewka.concordiapi.Subtasks.Updaters.TaskUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskPUTUpdaterWithTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubtaskTaskPUTUpdater implements SubtaskPUTUpdaterWithTeam {
    private Team team;
    private final SubtaskTaskUpdaterHelper subtaskTaskUpdaterHelper;

    @Autowired
    public SubtaskTaskPUTUpdater(SubtaskTaskUpdaterHelper subtaskTaskUpdaterHelper) {
        this.subtaskTaskUpdaterHelper = subtaskTaskUpdaterHelper;
    }

    @Override
    public void PUTUpdate(Subtask subtask, SubtaskRequestBody body) {
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
