package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.Subtask;
import com.example.javasprintbootapi.DatabaseModel.Task;
import com.example.javasprintbootapi.DatabaseModel.TaskRepository;
import com.example.javasprintbootapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    public List<Task> getAllTasksForUser(User user){
        List<Task> temp = new ArrayList<>();
        for (Task task : taskRepository.findAll()){
            if (task.getUsers().contains(user)){
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
                if (ChronoUnit.DAYS.between(task.getUpdateDate().toInstant(), LocalDateTime.now()) > days){
                    temp.add(task);
                }
            }
            return temp;
        }
    }

    public Task setUpdateToNow(Task task){
        task.setUpdateDate(new Date());
        return taskRepository.save(task);
    }

    public Task setUpdateToTime(Task task, Date date){
        task.setUpdateDate(date);
        return taskRepository.save(task);
    }

    public Task createTask(String name, User owner){
        Task task = new Task();
        task.setName(name);
        task.setOwner(owner);
        task.setTaskStatus(PublicVariables.TaskStatus.NEW);
        task.setCreationDate(new Date());
        return taskRepository.save(task);
    }

    public Task createTask(String name, String description, User owner, Set<User> users, Set<Subtask> subtasks){
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setTaskStatus(PublicVariables.TaskStatus.NEW);
        task.setCreationDate(new Date());
        task.setOwner(owner);
        task.setUsers(users);
        task.setSubtasks(subtasks);
        return taskRepository.save(task);
    }





}
