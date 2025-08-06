package com.patrykmarchewka.concordiapi.Subtasks.Updaters.NameUpdater;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Subtasks.Updaters.SubtaskPATCHUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubtaskNamePATCHUpdater implements SubtaskPATCHUpdater {

    private final SubtaskNameUpdaterHelper subtaskNameUpdaterHelper;

    @Autowired
    public SubtaskNamePATCHUpdater(SubtaskNameUpdaterHelper subtaskNameUpdaterHelper) {
        this.subtaskNameUpdaterHelper = subtaskNameUpdaterHelper;
    }

    @Override
    public void PATCHUpdate(Subtask subtask, SubtaskRequestBody body) {
        if (body.getName() != null){
            subtaskNameUpdaterHelper.sharedUpdate(subtask, body);
        }
    }
}
