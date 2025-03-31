package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DatabaseModel.Subtask;
import com.example.javaspringbootapi.DatabaseModel.SubtaskRepository;
import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubtaskService {

    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private TaskRepository taskRepository;

    public Subtask getSubtaskByID(long taskID, long subtaskID){
        return subtaskRepository.findSubtaskByIdAndTaskId(subtaskID,taskID).orElseThrow(() -> new RuntimeException("Subtask not found"));
    }

    @Transactional
    public Subtask createSubtask(long taskID, String name, String description){
        Task task = taskRepository.findById(taskID);
        Subtask subtask = new Subtask();
        subtask.setTask(task);
        subtask.setName(name);
        subtask.setDescription(description);
        subtask.setTaskStatus(PublicVariables.TaskStatus.NEW);
        return subtaskRepository.save(subtask);
    }

    @Transactional
    public void deleteSubtask(long taskID, long subtaskID){
        Subtask subtask = getSubtaskByID(taskID,subtaskID);
        Task task = subtask.getTask();
        task.getSubtasks().remove(subtask);
        subtaskRepository.delete(subtask);
        taskRepository.save(task);
    }

    public Subtask saveSubtask(Subtask subtask){
        return subtaskRepository.save(subtask);
    }



}
