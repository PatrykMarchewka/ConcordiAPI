package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.TaskStatus;

public interface SubtaskRequestBodyHelper {
    default SubtaskRequestBody createSubtaskRequestBody(){
        SubtaskRequestBody body = new SubtaskRequestBody();
        body.setName("Test Subtask");
        body.setDescription("Test Subtask Description");
        body.setTaskStatus(TaskStatus.NEW);
        return body;
    }

    default SubtaskRequestBody createSubtaskRequestBody(String name, String description, TaskStatus taskStatus){
        SubtaskRequestBody body = new SubtaskRequestBody();
        body.setName(name);
        body.setDescription(description);
        body.setTaskStatus(taskStatus);
        return body;
    }

    default SubtaskRequestBody createSubtaskRequestBodyPATCH(String name){
        SubtaskRequestBody body = new SubtaskRequestBody();
        body.setName(name);
        return body;
    }
}
