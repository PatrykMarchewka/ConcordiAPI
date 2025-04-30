package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.OnCreate;
import com.example.javaspringbootapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.example.javaspringbootapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.example.javaspringbootapi.DatabaseModel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
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
        if (myRole.isOwnerOrAdmin() || myRole.isManager() || task.getUsers().contains((User)authentication.getPrincipal())){
            for (Subtask sub : task.getSubtasks()){
                subtasks.add(new SubtaskMemberDTO(sub));
            }
            return ResponseEntity.ok(new APIResponse<>("Subtasks attached to this task",subtasks));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }
    }
    @PostMapping("/subtasks")
    public ResponseEntity<?> createSubtask(@PathVariable long teamID, @PathVariable long taskID, @RequestBody @Validated(OnCreate.class) SubtaskRequestBody body, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(taskID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.isOwnerOrAdmin() || myRole.isManager() || task.getUsers().contains((User)authentication.getPrincipal())){
            if (body.getName() == null || body.getDescription() == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("Subtask created", new SubtaskMemberDTO(subtaskService.createSubtask(team,taskID, body.getName(), body.getDescription()))));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }

    }

    @GetMapping("/subtasks/{ID}")
    public ResponseEntity<?> getSubtaskByID(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(taskID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.isOwnerOrAdmin() || myRole.isManager() || task.getUsers().contains((User)authentication.getPrincipal())){
            return ResponseEntity.ok(new APIResponse<>("Subtask details",new SubtaskMemberDTO(subtaskService.getSubtaskByID(taskID,ID))));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }
    }

    @PutMapping("/subtasks/{ID}")
    public ResponseEntity<?> putSubtask(@PathVariable long teamID, @PathVariable long taskID, @PathVariable long ID, @RequestBody @Validated(OnCreate.class) SubtaskRequestBody body, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(taskID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.isOwnerOrAdmin() || myRole.isManager() || task.getUsers().contains((User)authentication.getPrincipal())){
            Subtask subtask = subtaskService.getSubtaskByID(taskID,ID);
            subtask.setName(body.getName());
            subtask.setDescription(body.getDescription());
            subtask.setTaskStatus(body.getTaskStatus());
            subtaskService.saveSubtask(subtask);
            return ResponseEntity.ok(new APIResponse<>("Subtask changed", new SubtaskMemberDTO(subtask)));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }
    }

    @PatchMapping("/subtasks/{ID}")
    public ResponseEntity<?> patchSubtask(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, @RequestBody SubtaskRequestBody body, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(taskID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.isOwnerOrAdmin() || myRole.isManager() || task.getUsers().contains((User)authentication.getPrincipal())){
            Subtask subtask = subtaskService.getSubtaskByID(taskID,ID);
            if (body.getName() != null){
                subtask.setName(body.getName());
            }
            if (body.getDescription() != null){
                subtask.setDescription(body.getDescription());
            }
            if (body.getTaskStatus() != null){
                subtask.setTaskStatus(body.getTaskStatus());
            }
            subtaskService.saveSubtask(subtask);
            return ResponseEntity.ok(new APIResponse<>("Subtask updated",new SubtaskMemberDTO(subtask)));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }
    }
    @DeleteMapping("/subtasks/{ID}")
    public ResponseEntity<?> deleteSubtask(@PathVariable long teamID,@PathVariable long taskID, @PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.isOwnerOrAdmin()){
            subtaskService.deleteSubtask(taskID,ID);
            return ResponseEntity.ok(new APIResponse<>("Subtask deleted", null));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }
    }

}
