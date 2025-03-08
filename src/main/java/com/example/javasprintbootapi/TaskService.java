package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.Task;
import com.example.javasprintbootapi.DatabaseModel.TaskRepository;
import com.example.javasprintbootapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    public List<Task> getAllTasksWithoutUsers(){
        List<Task> temp = new ArrayList<>();

        for (Task task : taskRepository.findAll()){
            if (task.getUsers().isEmpty()){
                temp.add(task);
            }
        }

        return temp;
    }

    public List<Task> getAllTasksWithoutOwners(){
        List<Task> temp = new ArrayList<>();

        for (Task task : taskRepository.findAll()){
            if (task.getOwner() == null){
                temp.add(task);
            }
        }
        return temp;
    }

    public Task setOwnerToNull(Task task){
        task.setOwner(null);
        return taskRepository.save(task);
    }

    public Task setOwner(Task task, User user){
        task.setOwner(user);
        return taskRepository.save(task);
    }

    public List<Task> getAllTasksByStatus(PublicVariables.TaskStatus status){
        List<Task> temp = new ArrayList<>();

        for (Task task : taskRepository.findAll()){
            if (task.getTaskStatus().equals(status)){
                temp.add(task);
            }
        }
        return temp;
    }

    public Task setTaskStatus(Task task,PublicVariables.TaskStatus status){
        task.setTaskStatus(status);
        return taskRepository.save(task);
    }

    public List<Task> getAllTasksNoUpdatesIn(int days){

        if(days < 0){
            throw new IllegalArgumentException("Number of days cannot be negative!");
        }
        else{
            List<Task> temp = new ArrayList<>();

            for (Task task : taskRepository.findAll()){
                if (ChronoUnit.DAYS.between(task.getUpdateDate(), LocalDateTime.now()) > days){
                    temp.add(task);
                }
            }
            return temp;
        }
    }

    public Task setUpdateToNow(Task task){
        task.setUpdateDate(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Task setUpdateToTime(Task task, LocalDateTime date){
        task.setUpdateDate(date);
        return taskRepository.save(task);
    }




}
