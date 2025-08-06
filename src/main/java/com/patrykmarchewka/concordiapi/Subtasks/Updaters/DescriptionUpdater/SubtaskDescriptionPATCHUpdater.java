package com.patrykmarchewka.concordiapi.Subtasks.Updaters.DescriptionUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubtaskDescriptionPATCHUpdater implements SubtaskPATCHUpdater {

    private final SubtaskDescriptionUpdaterHelper subtaskDescriptionUpdaterHelper;

    @Autowired
    public SubtaskDescriptionPATCHUpdater(SubtaskDescriptionUpdaterHelper subtaskDescriptionUpdaterHelper) {
        this.subtaskDescriptionUpdaterHelper = subtaskDescriptionUpdaterHelper;
    }

    @Override
    public void PATCHUpdate(Subtask subtask, SubtaskRequestBody body) {
        if (body.getDescription() != null){
            subtaskDescriptionUpdaterHelper.sharedUpdate(subtask, body);
        }
    }
}
