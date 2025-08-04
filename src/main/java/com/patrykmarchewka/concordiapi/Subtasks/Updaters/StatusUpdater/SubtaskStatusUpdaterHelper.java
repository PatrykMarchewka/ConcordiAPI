package com.patrykmarchewka.concordiapi.Subtasks.Updaters.StatusUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import org.springframework.stereotype.Service;

@Service
public class SubtaskStatusUpdaterHelper {
    void sharedUpdate(Subtask subtask, SubtaskRequestBody body){
        subtask.setTaskStatus(body.getTaskStatus());
    }
}
