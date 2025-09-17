package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.TaskStatus;

import java.util.Set;

public interface TaskRequestBodyHelper {
    default TaskRequestBody createTaskRequestBody(){
        TaskRequestBody body = new TaskRequestBody();
        body.setName("Test task");
        body.setDescription("Test description");
        body.setTaskStatus(TaskStatus.NEW);
        return body;
    }

    default TaskRequestBody createTaskRequestBody(String name, String description, TaskStatus taskStatus, Set<Integer> userIDs){
        TaskRequestBody body = new TaskRequestBody();
        body.setName(name);
        body.setDescription(description);
        body.setTaskStatus(taskStatus);
        body.setUsers(userIDs);
        return body;
    }
}
