package com.patrykmarchewka.concordiapi.Subtasks.Updaters.DescriptionUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import org.springframework.stereotype.Service;

@Service
public class SubtaskDescriptionUpdaterHelper {
    void sharedUpdate(Subtask subtask, SubtaskRequestBody body){ subtask.setDescription(body.getDescription()); }
}
