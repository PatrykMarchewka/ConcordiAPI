package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class SubtaskUpdatersService {

    private final SubtaskUpdatersCREATE subtaskUpdatersCREATE;
    private final SubtaskUpdatersPUT subtaskUpdatersPUT;
    private final SubtaskUpdatersPATCH subtaskUpdatersPATCH;

    @Autowired
    public SubtaskUpdatersService(SubtaskUpdatersCREATE subtaskUpdatersCREATE, SubtaskUpdatersPUT subtaskUpdatersPUT, SubtaskUpdatersPATCH subtaskUpdatersPATCH) {
        this.subtaskUpdatersCREATE = subtaskUpdatersCREATE;
        this.subtaskUpdatersPUT = subtaskUpdatersPUT;
        this.subtaskUpdatersPATCH = subtaskUpdatersPATCH;
    }

    public void update(Subtask subtask, SubtaskRequestBody body, Supplier<Task> task, UpdateType type){
        switch (type){
            case CREATE -> subtaskUpdatersCREATE.applyCreateUpdates(subtask,body,task);
            case PUT -> subtaskUpdatersPUT.applyPutUpdates(subtask, body);
            case PATCH -> subtaskUpdatersPATCH.applyPatchUpdates(subtask, body);
            case null, default -> throw new BadRequestException("Called update type that isn't CREATE/PUT/PATCH");
        }
    }
}
