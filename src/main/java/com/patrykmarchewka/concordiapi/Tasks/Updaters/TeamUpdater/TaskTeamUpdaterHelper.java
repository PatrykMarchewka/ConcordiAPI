package com.patrykmarchewka.concordiapi.Tasks.Updaters.TeamUpdater;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import org.springframework.stereotype.Service;

@Service
public class TaskTeamUpdaterHelper {

    void sharedUpdate(Task task, Team team){
        team.addTask(task);
    }
}
