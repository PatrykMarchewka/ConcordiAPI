package com.patrykmarchewka.concordiapi.Tasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void update(Task task, TaskRequestBody body, UpdateType type){
        switch (type){
            case CREATE -> taskUpdatersCREATE.applyCreateUpdates(task, body);
            case PUT -> taskUpdatersPUT.applyPutUpdates(task, body);
            case PATCH -> taskUpdatersPATCH.applyPatchUpdates(task, body);
            case null, default -> throw new NotImplementedException("Called update type that isn't CREATE/PUT/PATCH");
        }
    }
}
