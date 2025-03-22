package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.Subtask;
import com.example.javasprintbootapi.DatabaseModel.Task;
import com.example.javasprintbootapi.DatabaseModel.TaskRepository;
import com.example.javasprintbootapi.DatabaseModel.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.*;

@RestController
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;


    @GetMapping("/tasks")
    public List<Task> getAllTasks(Authentication authentication){
        if (authentication == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"No authentication!");
        }
        Map<String,String> user = UserController.userInfo(authentication);
        if (user.get("role").equals(PublicVariables.UserRole.ADMIN.name())){
            return taskService.getAllTasks();
        }
        else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"This action requires admin access");
        }
    }

    @GetMapping("/tasks/me")
    public List<Task> getAllTasksAssignedToMe(Authentication authentication){
        if (authentication == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"No authentication!");
        }
        return taskService.getAllTasksForUser((User)authentication.getPrincipal());

    }

    @GetMapping("tasks/{ID}")
    public Task getTaskByID(@PathVariable long ID, Authentication authentication){
        Task task = taskRepository.findById(ID);

        User user = (User)authentication.getPrincipal();
        if (task.getUsers().contains(user) || task.getOwner().equals(user) || user.getRole().equals(PublicVariables.UserRole.ADMIN)){
            return task;
        }
        else{
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You cant view this task");
        }
        return null;
    }

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@RequestBody Map<String,String> body, Authentication authentication) throws JsonProcessingException {
        if (authentication == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"No authentication!");
        }
        if (!body.containsKey("name") || !body.containsKey("owner")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields");
        }
        String name = body.get("name");
        User owner = userService.getUserByLogin(body.get("owner"));

        if (body.containsKey("description") && body.containsKey("users") && body.containsKey("subtasks")){
            String description = body.get("description");
            ObjectMapper objectMapper = new ObjectMapper();
            Set<User> users = objectMapper.readValue(body.get("users"), new TypeReference<Set<User>>() {});
            Set<Subtask> subtasks = objectMapper.readValue(body.get("subtasks"), new TypeReference<Set<Subtask>>() {});
            taskService.createTask(name,description,owner,users,subtasks);
        }
        else{
            taskService.createTask(name,owner);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Task created!");
    }

    @PatchMapping("/tasks/{ID}")
    public ResponseEntity<?> updateTask(@PathVariable long ID, @RequestBody Map<String,String> body, Authentication authentication){
        Task task = taskRepository.findById(ID);
        if (task != null){
            body.forEach((key,value) -> {
                Field field = ReflectionUtils.findField(Task.class, key);
                if (List.of("id","creationDate").contains(key)){
                    throw new IllegalArgumentException("You cannot patch sensitive fieleds: " + key);
                }
                if (List.of("owner").contains(key) && !((User)authentication.getPrincipal()).getRole().name().equalsIgnoreCase("admin")){
                    throw new IllegalArgumentException("You cannot modify admin field as non-admin:" + key);
                }
                if (field != null){
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, task,value);
                }
            });
            task.setUpdateDate(new Date());
            taskRepository.save(task);
            return  ResponseEntity.ok("Task updated!");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found!");
        }
    }

}
