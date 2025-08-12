package com.patrykmarchewka.concordiapi.Subtasks.Updaters.TaskUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskPATCHUpdaterWithTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubtaskTaskPATCHUpdater implements SubtaskPATCHUpdaterWithTeam {

    private Team team;
    private final SubtaskTaskUpdaterHelper subtaskTaskUpdaterHelper;

    @Autowired
    public SubtaskTaskPATCHUpdater(SubtaskTaskUpdaterHelper subtaskTaskUpdaterHelper) {
        this.subtaskTaskUpdaterHelper = subtaskTaskUpdaterHelper;
    }


    @Override
    public void PATCHUpdate(Subtask subtask, SubtaskRequestBody body) {
        if (body.getTask() != null){
            if (this.team == null){
                throw new BadRequestException("The team is set to null");
            }
            subtaskTaskUpdaterHelper.sharedUpdate(subtask,body,team);
        }
    }

    @Override
    public void setTeam(Team team) {
        this.team = team;
    }
}
