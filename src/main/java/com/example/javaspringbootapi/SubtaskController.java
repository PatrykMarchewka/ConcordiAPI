package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.SubtaskMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/teams/{teamID}/tasks/{taskID}")
public class SubtaskController {

    @Autowired
    private SubtaskService subtaskService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;

    @GetMapping("/subtasks")
    public ResponseEntity<?> getSubtasks(@PathVariable long teamID,@PathVariable long taskID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(taskID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        Set<SubtaskMemberDTO> subtasks = new HashSet<>();
        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER) || task.getUsers().contains((User)authentication.getPrincipal())){
            for (Subtask sub : task.getSubtasks()){
                subtasks.add(new SubtaskMemberDTO(sub));
            }
            return ResponseEntity.ok(subtasks);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }
    @PostMapping("/subtasks")
    public ResponseEntity<?> createSubtask(@PathVariable long teamID,@PathVariable long taskID, @RequestBody Map<String,String> body, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(taskID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER) || task.getUsers().contains((User)authentication.getPrincipal())){
            subtaskService.createSubtask(taskID,body.get("name").toString(),body.get("description").toString());
            return ResponseEntity.status(HttpStatus.CREATED).body("Subtask created");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }

    }

    @GetMapping("/subtasks/{ID}")
    public ResponseEntity<?> getSubtaskByID(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(taskID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER) || task.getUsers().contains((User)authentication.getPrincipal())){
            return ResponseEntity.ok(new SubtaskMemberDTO(subtaskService.getSubtaskByID(taskID,ID)));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PutMapping("/subtasks/{ID}")
    public ResponseEntity<?> putSubtask(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, @RequestBody Map<String,Object> body, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(taskID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER) || task.getUsers().contains((User)authentication.getPrincipal())){
            Subtask subtask = subtaskService.getSubtaskByID(taskID,ID);
            subtask.setName(body.get("name").toString());
            subtask.setDescription(body.get("description").toString());
            subtask.setTaskStatus(PublicVariables.TaskStatus.fromString(body.get("taskStatus").toString()));
            subtaskService.saveSubtask(subtask);
            return ResponseEntity.ok("Subtask fully changed");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PatchMapping("/subtasks/{ID}")
    public ResponseEntity<?> patchSubtask(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, @RequestBody Map<String,Object> body, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(taskID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER) || task.getUsers().contains((User)authentication.getPrincipal())){
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
            subtaskService.saveSubtask(subtask);
            return ResponseEntity.ok("Subtask updated");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }



    }

    @DeleteMapping("/subtasks/{ID}")
    public ResponseEntity<?> deleteSubtask(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(taskID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.equals(PublicVariables.UserRole.ADMIN)){
            subtaskService.deleteSubtask(taskID,ID);
            return ResponseEntity.ok("Subtask deleted!");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

}
