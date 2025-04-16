package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.OnCreate;
import com.example.javaspringbootapi.DTO.TaskDTO.TaskManagerDTO;
import com.example.javaspringbootapi.DTO.TaskDTO.TaskMemberDTO;
import com.example.javaspringbootapi.DTO.TaskDTO.TaskRequestBody;
import com.example.javaspringbootapi.DatabaseModel.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/teams/{teamID}")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;
    @Autowired
    private SubtaskService subtaskService;


    @GetMapping("/tasks")
    public ResponseEntity<?> getAllTasks(@PathVariable long teamID,Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);

        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER)){
            Set<TaskManagerDTO> filteredTasks = new HashSet<>();
            for (Task task : taskService.getAllTasks(team)){
                filteredTasks.add(new TaskManagerDTO(task));
            }
            return ResponseEntity.ok(new APIResponse<>("All tasks for the team",filteredTasks));
        }
        else if (myRole.equals(PublicVariables.UserRole.MEMBER)){
            User user = (User)authentication.getPrincipal();
            Set<TaskMemberDTO> filteredTasks = new HashSet<>();
            for (Task task : user.getTasks()){
                filteredTasks.add(new TaskMemberDTO(task));
            }
            return ResponseEntity.ok(new APIResponse<>("All tasks assigned to me",filteredTasks));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@PathVariable long teamID, @RequestBody @Validated(OnCreate.class) TaskRequestBody body){
        if (body.getName() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields");
        }
        Team team = teamService.getTeamByID(teamID);
        Set<User> userSet = null;
        if (body.getUsers() != null){
            userSet = new HashSet<>();
            for (int id : body.getUsers()){
                if (userService.checkIfUserExistsInATeam(userService.getUserByID((long)id), team)){
                    userSet.add(userService.getUserByID((long)id));
                }
                else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tried to add user that is not part of the team");
                }

            }
        }
        Task task = taskService.createTask(body.getName(), body.getDescription(),team,userSet,body.getTaskStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("Created new task",new TaskMemberDTO(task)));
    }

    @GetMapping("/tasks/me")
    public ResponseEntity<?> getAllTasksAssignedToMe(@PathVariable long teamID,Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER)){
            Set<TaskManagerDTO> tasks = new HashSet<>();
            for (Task task : ((User) authentication.getPrincipal()).getTasks()){
                tasks.add(new TaskManagerDTO(task));
            }
            return ResponseEntity.ok(new APIResponse<>("Tasks assigned to me",tasks));
        }
        else if(myRole.equals(PublicVariables.UserRole.MEMBER)){
            Set<TaskMemberDTO> tasks = new HashSet<>();
            for (Task task : ((User) authentication.getPrincipal()).getTasks()){
                tasks.add(new TaskMemberDTO(task));
            }
            return ResponseEntity.ok(new APIResponse<>("Tasks assigned to me",tasks));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @GetMapping("/tasks/{ID}")
    public ResponseEntity<?> getTaskByID(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(ID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER)){
            return ResponseEntity.ok(new APIResponse<>("Task details", new TaskManagerDTO(task)));
        }
        else if(task.getUsers().contains((User)authentication.getPrincipal())){
            return ResponseEntity.ok(new APIResponse<>("Task details", new TaskMemberDTO(task)));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PutMapping("/tasks/{ID}")
    public ResponseEntity<?> putTask(@PathVariable long teamID, @PathVariable long ID, @RequestBody @Validated(OnCreate.class) TaskRequestBody body, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(ID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);

        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER) || task.getUsers().contains((User)authentication.getPrincipal())){
            try{
                task.setName(body.getName());
                task.setDescription(body.getDescription());
                task.setTaskStatus(body.getTaskStatus());
                for (User user : task.getUsers()){
                    taskService.removeUserFromTask(team,ID,user);
                }
                for (int id : body.getUsers()){
                    if (userService.checkIfUserExistsInATeam(userService.getUserByID((long)id), team)){
                        taskService.addUserToTask(team,ID, userService.getUserByID((long)id));
                    }
                    else{
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tried to add user that is not part of the team");
                    }
                }

                for (Subtask subtask : task.getSubtasks()){
                    taskService.removeSubtaskFromTask(team,ID,subtask);
                }
                for (int id : body.getSubtasks()){
                    taskService.addSubtaskToTask(team,ID, subtaskService.getSubtaskByID(ID,id));
                }
                taskService.saveTask(task);
                return ResponseEntity.ok(new APIResponse<>("Task fully changed",new TaskMemberDTO(task)));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MenuOptions.CouldntCompleteOperation());
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PatchMapping("/tasks/{ID}")
    public ResponseEntity<?> patchTask(@PathVariable long teamID,@PathVariable long ID, @RequestBody TaskRequestBody body, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(ID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);

        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER)){
            try{
                if (body.getName() != null){
                    task.setName(body.getName());
                }
                if (body.getDescription() != null){
                    task.setDescription(body.getDescription());
                }
                if (body.getTaskStatus() != null){
                    task.setTaskStatus(body.getTaskStatus());
                }
                if (body.getUsers() != null){
                    for (User user : task.getUsers()){
                        taskService.removeUserFromTask(team,ID,user);
                    }
                    for (int id : body.getUsers()){
                        if (userService.checkIfUserExistsInATeam(userService.getUserByID((long)id), team)){
                            taskService.addUserToTask(team,ID, userService.getUserByID((long)id));
                        }
                        else{
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tried to add user that is not part of the team");
                        }
                    }

                }
                if (body.getSubtasks() != null){
                    for (Subtask subtask : task.getSubtasks()){
                        taskService.removeSubtaskFromTask(team,ID,subtask);
                    }
                    for (int id : body.getSubtasks()){
                        taskService.addSubtaskToTask(team,ID, subtaskService.getSubtaskByID(ID,id));
                    }
                }
                taskService.saveTask(task);
                return ResponseEntity.ok(new APIResponse<>("Task updated",new TaskMemberDTO(task)));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MenuOptions.CouldntCompleteOperation());
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @DeleteMapping("/tasks/{ID}")
    public ResponseEntity<?> deleteTask(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.equals(PublicVariables.UserRole.ADMIN)){
            taskService.deleteTaskByID(ID,team);
            return ResponseEntity.ok(new APIResponse<>("Task deleted",null));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PostMapping("/tasks/{ID}/users/{userID}")
    public ResponseEntity<?> addOneUserToTask(@PathVariable long teamID, @PathVariable long ID,@PathVariable long userID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        try {
            User user = userService.getUserByID(userID);
            PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
            PublicVariables.UserRole role = teamUserRoleService.getRole(user,team);
            if (role.compareTo(myRole) >= 0){
                taskService.addUserToTask(team, ID, user);
                return ResponseEntity.ok(new APIResponse<>("User added to task",new TaskMemberDTO(taskService.getTaskByID(ID,team))));
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MenuOptions.CouldntCompleteOperation());
        }

    }

    @DeleteMapping("/tasks/{ID}/users/{userID}")
    public ResponseEntity<?> deleteOneUserFromTask(@PathVariable long teamID, @PathVariable long ID,@PathVariable long userID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        try {
            User user = userService.getUserByID(userID);
            PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
            PublicVariables.UserRole role = teamUserRoleService.getRole(user,team);
            if (role.compareTo(myRole) >= 0){
                taskService.removeUserFromTask(team, ID, user);
                return ResponseEntity.ok(new APIResponse<>("User removed from task",new TaskMemberDTO(taskService.getTaskByID(ID,team))));
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MenuOptions.CouldntCompleteOperation());
        }
    }

}
