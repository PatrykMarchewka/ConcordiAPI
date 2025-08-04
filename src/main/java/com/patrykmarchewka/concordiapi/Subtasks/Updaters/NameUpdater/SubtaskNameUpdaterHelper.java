package com.patrykmarchewka.concordiapi.Subtasks.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import org.springframework.stereotype.Service;

@Service
public class SubtaskNameUpdaterHelper {
    void sharedUpdate(Subtask subtask, SubtaskRequestBody body){
        subtask.setName(body.getName());
    }
}
