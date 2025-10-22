package com.patrykmarchewka.concordiapi.Subtasks.Updaters;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
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

    public void createUpdate(Subtask subtask, SubtaskRequestBody body, Supplier<Task> task){
        subtaskUpdatersCREATE.applyCreateUpdates(subtask, body, task);
    }

    public void putUpdate(Subtask subtask, SubtaskRequestBody body){
        subtaskUpdatersPUT.applyPutUpdates(subtask, body);
    }

    public void patchUpdate(Subtask subtask, SubtaskRequestBody body){
        subtaskUpdatersPATCH.applyPatchUpdates(subtask, body);
    }
}
