package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DatabaseModel.*;
import com.patrykmarchewka.concordiapi.DatabaseModel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubtaskService subtaskService;
    @Autowired
    @Lazy
    private TeamService teamService;
    @Autowired
    private UserService userService;

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }


    public Set<Task> getAllTasks(Team team){
        return taskRepository.findByTeam(team);
    }

    public Task getTaskByID(long ID,Team team){
        return taskRepository.findByIdAndTeam(ID,team);
    }

    @Transactional(readOnly = true)
    public Set<Task> getAllTasksWithoutUsers(){
        Set<Task> temp = new HashSet<>();

        for (Task task : taskRepository.findAll()){
            if (task.getUsers().isEmpty()){
                temp.add(task);
            }
        }

        return temp;
    }


    @Transactional(readOnly = true)
    public Set<Task> getAllTasksForUser(User user){
        Set<Task> temp = new HashSet<>();
        for (Task task : taskRepository.findAll()){
            if (task.getUsers().contains(user)){
                temp.add(task);
            }
        }
        return temp;
    }


    @Transactional(readOnly = true)
    public Set<Task> getAllTasksByStatus(PublicVariables.TaskStatus status){
        Set<Task> temp = new HashSet<>();

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

    @Transactional(readOnly = true)
    public Set<Task> getAllTasksNoUpdatesIn(int days){
        if(days < 0){
            throw new IllegalArgumentException("Number of days cannot be negative!");
        }
        else{
            Set<Task> temp = new HashSet<>();
            for (Task task : taskRepository.findAll()){
                if (ChronoUnit.DAYS.between(task.getUpdateDate(), OffsetDateTime.now()) > days){
                    temp.add(task);
                }
            }
            return temp;
        }
    }

    public Task setUpdateToTime(Task task, OffsetDateTime date){
        task.setUpdateDate(date);
        return taskRepository.save(task);
    }

    @Transactional
    public Task createTask(String name, @Nullable String description, Team team, @Nullable Set<User> users, PublicVariables.TaskStatus status){
        Task task = new Task();
        task.setTeam(team);
        task.setName(name);
        if (description != null){
            task.setDescription(description);
        }
        task.setTaskStatus((status == null) ? PublicVariables.TaskStatus.NEW : status);
        task.setCreationDate(OffsetDateTime.now());
        task.setUpdateDate(OffsetDateTime.now());
        taskRepository.save(task);
        team.getTasks().add(task);
        teamService.saveTeam(team);
        for (User user : users){
            addUserToTask(team, task.getID(), user);
        }


        return task;
    }

    @Transactional
    public void deleteTaskByID(long ID, Team team){
        Task task = taskRepository.findByIdAndTeam(ID,team);
        for (User user : task.getUsers()){
            user.getTasks().remove(task);
            userRepository.save(user);
        }
        team.getTasks().remove(task);
        teamService.saveTeam(team);
    }

    public Task saveTask(Task task){
        task.setUpdateDate(OffsetDateTime.now());
        return taskRepository.save(task);
    }


    @Transactional
    public void addUserToTask(Team team, long taskID, User user){
        Task task = taskRepository.findByIdAndTeam(taskID,team);
        task.getUsers().add(user);
        taskRepository.save(task);
        user.getTasks().add(task);
        userRepository.save(user);
    }

    @Transactional
    public void removeUserFromTask(Team team, long taskID, User user){
        Task task = taskRepository.findByIdAndTeam(taskID,team);
        task.getUsers().remove(user);
        taskRepository.save(task);
        user.getTasks().remove(task);
        userRepository.save(user);
    }

    @Transactional
    public void addSubtaskToTask(Team team, long taskID, Subtask subtask){
        Task task = taskRepository.findByIdAndTeam(taskID,team);
        task.getSubtasks().add(subtask);
        taskRepository.save(task);
        subtask.setTask(task);
        subtaskService.saveSubtask(subtask);
    }

    @Transactional
    public void removeSubtaskFromTask(Team team, long taskID, Subtask subtask){
        Task task = taskRepository.findByIdAndTeam(taskID,team);
        task.getSubtasks().remove(subtask);
        taskRepository.save(task);
        subtaskService.deleteSubtask(taskID, subtask.getID());
    }

    public Subtask getSubtaskByID(long taskID,long subtaskID){
        return subtaskService.getSubtaskByID(taskID,subtaskID);
    }







}
