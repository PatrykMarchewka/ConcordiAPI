package com.patrykmarchewka.concordiapi.Tasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class TaskUpdatersCREATE {
    private final List<TaskCREATEUpdater> updaters;
    private final List<TaskCREATEUpdaterBasic> updaterBasics;
    private final List<TaskCREATEUpdaterBasicWithTeam> updaterBasicWithTeams;

    @Autowired
    public TaskUpdatersCREATE(List<TaskCREATEUpdater> updaters, List<TaskCREATEUpdaterBasic> updaterBasics, List<TaskCREATEUpdaterBasicWithTeam> updaterBasicWithTeams) {
        this.updaters = updaters;
        this.updaterBasics = updaterBasics;
        this.updaterBasicWithTeams = updaterBasicWithTeams;
    }

    /**
     * Applies CREATE updates for the Task given the TaskRequestBody details, should be only called from {@link TaskUpdatersService#update(Task, TaskRequestBody, UpdateType, Supplier)}
     * @param task Task to create
     * @param body TaskRequestBody with information to update
     * @param team Team in which task is created
     */
    void applyCreateUpdates(Task task, TaskRequestBody body, Supplier<Team> team){

        for (TaskCREATEUpdater updater : updaters){
            updater.CREATEUpdate(task, body);
        }

        for (TaskCREATEUpdaterBasicWithTeam updaterBasicWithTeam : updaterBasicWithTeams){
            updaterBasicWithTeam.setTeam(team.get());
            updaterBasicWithTeam.CREATEUpdate(task);
        }

        for (TaskCREATEUpdaterBasic updaterBasic : updaterBasics){
            updaterBasic.CREATEUpdate(task);
        }
    }

}
