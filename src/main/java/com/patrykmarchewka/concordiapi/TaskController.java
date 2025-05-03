package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.*;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/teams/{teamID}")
@SecurityRequirement(name = "BearerAuth")
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

    @Operation(summary = "Get all tasks",description = "Get all tasks if Owner/Admin/Manager or just tasks assigned to me if Member")
    @GetMapping("/tasks")
    public ResponseEntity<?> getAllTasks(@PathVariable long teamID,Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);

        if (myRole.isOwnerOrAdmin() || myRole.isManager()){
            Set<TaskManagerDTO> filteredTasks = new HashSet<>();
            for (Task task : taskService.getAllTasks(team)){
                filteredTasks.add(new TaskManagerDTO(task));
            }
            return ResponseEntity.ok(new APIResponse<>("All tasks for the team",filteredTasks));
        }
        else if (myRole.isMember()){
            User user = (User)authentication.getPrincipal();
            Set<TaskMemberDTO> filteredTasks = new HashSet<>();
            for (Task task : user.getTasks()){
                filteredTasks.add(new TaskMemberDTO(task));
            }
            return ResponseEntity.ok(new APIResponse<>("All tasks assigned to me",filteredTasks));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }
    }

    @Operation(summary = "Create new task", description = "Creates a new task with specified information")
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
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>("Tried to add user that isn't part of the team",null));
                }

            }
        }
        Task task = taskService.createTask(body.getName(), body.getDescription(),team,userSet,body.getTaskStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(new APIResponse<>("Created new task",new TaskMemberDTO(task)));
    }

    @Operation(summary = "Get all tasks assigned to me", description = "Gets all tasks assigned to me even if user is Owner/Admin/Manager")
    @GetMapping("/tasks/me")
    public ResponseEntity<?> getAllTasksAssignedToMe(@PathVariable long teamID,Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.isOwnerOrAdmin() || myRole.isManager()){
            Set<TaskManagerDTO> tasks = new HashSet<>();
            for (Task task : ((User) authentication.getPrincipal()).getTasks()){
                tasks.add(new TaskManagerDTO(task));
            }
            return ResponseEntity.ok(new APIResponse<>("Tasks assigned to me",tasks));
        }
        else if(myRole.isMember()){
            Set<TaskMemberDTO> tasks = new HashSet<>();
            for (Task task : ((User) authentication.getPrincipal()).getTasks()){
                tasks.add(new TaskMemberDTO(task));
            }
            return ResponseEntity.ok(new APIResponse<>("Tasks assigned to me",tasks));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }
    }

    @Operation(summary = "Get information about task",description = "Get information about specific task by its ID")
    @GetMapping("/tasks/{ID}")
    public ResponseEntity<?> getTaskByID(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(ID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.isOwnerOrAdmin() || myRole.isManager()){
            return ResponseEntity.ok(new APIResponse<>("Task details", new TaskManagerDTO(task)));
        }
        else if(task.getUsers().contains((User)authentication.getPrincipal())){
            return ResponseEntity.ok(new APIResponse<>("Task details", new TaskMemberDTO(task)));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }
    }

    @Operation(summary = "Edit completely task",description = "Edits the entire task with all required fields")
    @PutMapping("/tasks/{ID}")
    public ResponseEntity<?> putTask(@PathVariable long teamID, @PathVariable long ID, @RequestBody @Validated(OnCreate.class) TaskRequestBody body, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(ID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);

        if (myRole.isOwnerOrAdmin() || myRole.isManager() || task.getUsers().contains((User)authentication.getPrincipal())){
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
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>("Tried to add user that isn't part of the team",null));
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(MenuOptions.CouldntCompleteOperation(),null));
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }
    }

    @Operation(summary = "Edit task",description = "Edit task fields")
    @PatchMapping("/tasks/{ID}")
    public ResponseEntity<?> patchTask(@PathVariable long teamID,@PathVariable long ID, @RequestBody TaskRequestBody body, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(ID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);

        if (myRole.isOwnerOrAdmin() || myRole.isManager()){
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
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>("Tried to add user that isn't part of the team",null));
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(MenuOptions.CouldntCompleteOperation(),null));
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }
    }

    @Operation(summary = "Delete the task", description = "Delete the entire task completely")
    @DeleteMapping("/tasks/{ID}")
    public ResponseEntity<?> deleteTask(@PathVariable long teamID,@PathVariable long ID, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);
        if (myRole.isOwnerOrAdmin()){
            if (taskService.getTaskByID(ID,team) != null){
                taskService.deleteTaskByID(ID,team);
                return ResponseEntity.ok(new APIResponse<>("Task deleted",null));
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>("Couldn't find task with specified ID",null));
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
        }
    }

    @Operation(summary = "Attach user to task",description = "Assigns the task to given user")
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(MenuOptions.CouldntCompleteOperation(),null));
        }

    }

    @Operation(summary = "Remove user from task", description = "Removes user from the task")
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(MenuOptions.NoPermissionsMessage(),null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(MenuOptions.CouldntCompleteOperation(),null));
        }
    }

}
