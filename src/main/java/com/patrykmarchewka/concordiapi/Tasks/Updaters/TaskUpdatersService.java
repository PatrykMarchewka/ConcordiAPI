package com.patrykmarchewka.concordiapi.Tasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class TaskUpdatersService {

    private final TaskUpdatersCREATE taskUpdatersCREATE;
    private final TaskUpdatersPUT taskUpdatersPUT;
    private final TaskUpdatersPATCH taskUpdatersPATCH;


    @Autowired
    public TaskUpdatersService(TaskUpdatersCREATE taskUpdatersCREATE, TaskUpdatersPUT taskUpdatersPUT, TaskUpdatersPATCH taskUpdatersPATCH) {
        this.taskUpdatersCREATE = taskUpdatersCREATE;
        this.taskUpdatersPUT = taskUpdatersPUT;
        this.taskUpdatersPATCH = taskUpdatersPATCH;
    }

    public void createUpdate(Task task, TaskRequestBody body, Supplier<Team> teamSupplier){
        taskUpdatersCREATE.applyCreateUpdates(task, body, teamSupplier);
    }

    public void putUpdate(Task task, TaskRequestBody body){
        taskUpdatersPUT.applyPutUpdates(task, body);
    }

    public void patchUpdate(Task task, TaskRequestBody body){
        taskUpdatersPATCH.applyPatchUpdates(task, body);
    }
}
