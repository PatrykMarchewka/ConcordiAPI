package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks/{taskID}")
public class SubtaskController {

    @Autowired
    private SubtaskService subtaskService;
    @Autowired
    private SubtaskRepository subtaskRepository;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/subtasks")
    public ResponseEntity<?> getSubtasks(@PathVariable long taskID, Authentication authentication){
        User user = ((User)authentication.getPrincipal());
        Task task = taskRepository.findById(taskID);
        if (!(user.getRole().name().equalsIgnoreCase("admin")) && !(task.getOwner().equals(user)) && !(task.getUsers().contains(user))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to do this action");
        }
        return ResponseEntity.ok(task.getSubtasks());
    }

    @GetMapping("/subtasks/{ID}")
    public ResponseEntity<?> getSubtaskByID(@PathVariable long taskID, @PathVariable long ID, Authentication authentication){
        User user = ((User)authentication.getPrincipal());
        Task task = taskRepository.findById(taskID);
        if (!(user.getRole().name().equalsIgnoreCase("admin")) && !(task.getOwner().equals(user)) && !(task.getUsers().contains(user))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to do this action");
        }
        Subtask subtask = subtaskService.getSubtaskByID(taskID,ID);
        return ResponseEntity.ok(subtask);
    }

    @PostMapping("/subtasks")
    public ResponseEntity<?> createSubtask(@PathVariable long taskID, @RequestBody Map<String,String> body, Authentication authentication){
        User user = ((User)authentication.getPrincipal());
        Task task = taskRepository.findById(taskID);
        if (!(user.getRole().name().equalsIgnoreCase("admin")) && !(task.getOwner().equals(user)) && !(task.getUsers().contains(user))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to do this action");
        }
        Subtask subtask = subtaskService.createSubtask(taskID,body.get("name"),body.get("description"));
        task.getSubtasks().add(subtask);
        taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body("Subtask created");
    }

    @PutMapping("/subtasks/{ID}")
    public ResponseEntity<?> putSubtask(@PathVariable long taskID, @PathVariable long ID, @RequestBody Map<String,Object> body, Authentication authentication){
        User user = ((User)authentication.getPrincipal());
        Task task = taskRepository.findById(taskID);
        if (!(user.getRole().name().equalsIgnoreCase("admin")) && !(task.getOwner().equals(user)) && !(task.getUsers().contains(user))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to do this action");
        }


        Subtask subtask = subtaskService.getSubtaskByID(taskID,ID);
        subtask.setName(body.get("name").toString());
        subtask.setDescription(body.get("description").toString());
        subtask.setTaskStatus(PublicVariables.TaskStatus.fromString(body.get("taskStatus").toString()));
        Task newTask = taskRepository.findById(Long.valueOf(body.get("task").toString())).orElseThrow();
        subtask.setTask(newTask);
        subtaskRepository.save(subtask);
        return ResponseEntity.ok("Subtask updated");
    }

    @PatchMapping("/subtasks/{ID}")
    public ResponseEntity<?> patchSubtask(@PathVariable long taskID, @PathVariable long ID, @RequestBody Map<String,Object> body, Authentication authentication){
        User user = ((User)authentication.getPrincipal());
        Task task = taskRepository.findById(taskID);
        if (!(user.getRole().name().equalsIgnoreCase("admin")) && !(task.getOwner().equals(user)) && !(task.getUsers().contains(user))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to do this action");
        }


        Subtask subtask = subtaskService.getSubtaskByID(taskID,ID);
        if (body.containsKey("name")){
            subtask.setName(body.get("name").toString());
        }
        if (body.containsKey("description")){
            subtask.setDescription(body.get("description").toString());
        }
        if (body.containsKey("taskStatus")){
            subtask.setTaskStatus(PublicVariables.TaskStatus.fromString(body.get("taskStatus").toString()));
        }
        if (body.containsKey("task")){
            Task newTask = taskRepository.findById(Long.valueOf(body.get("task").toString())).orElseThrow();
            subtask.setTask(newTask);
        }
        subtaskRepository.save(subtask);
        return ResponseEntity.ok("Subtask updated");
    }

    @DeleteMapping("/subtasks/{ID}")
    public ResponseEntity<?> deleteSubtask(@PathVariable long taskID, @PathVariable long ID, Authentication authentication){
        User user = ((User)authentication.getPrincipal());
        Task task = taskRepository.findById(taskID);
        if (!(user.getRole().name().equalsIgnoreCase("admin")) && !(task.getOwner().equals(user)) && !(task.getUsers().contains(user))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to do this action");
        }



        subtaskService.deleteSubtask(taskID,ID);
        return ResponseEntity.ok("Subtask deleted");
    }

}
