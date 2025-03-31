package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.TaskManagerDTO;
import com.example.javaspringbootapi.DTO.TaskMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams/{teamID}")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    @Autowired
    private SubtaskService subtaskService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;


    @GetMapping("/tasks")
    public ResponseEntity<?> getAllTasks(@PathVariable long teamID,Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);

        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER)){
            Set<TaskMemberDTO> filteredTasks = new HashSet<>();
            for (Task task : taskService.getAllTasks(team)){
                filteredTasks.add(new TaskMemberDTO(task));
            }
            return ResponseEntity.ok(filteredTasks);
        }
        else if (myRole.equals(PublicVariables.UserRole.MEMBER)){
            User user = (User)authentication.getPrincipal();
            Set<TaskMemberDTO> filteredTasks = new HashSet<>();
            for (Task task : user.getTasks()){
                filteredTasks.add(new TaskMemberDTO(task));
            }
            return ResponseEntity.ok(filteredTasks);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@PathVariable long teamID,@RequestBody Map<String,String> body, Authentication authentication) throws JsonProcessingException {
        if (!body.containsKey("name")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields");
        }
        String name = body.get("name");
        Team team = teamService.getTeamByID(teamID);
        //TODO: Test this
        Task task = new Task();
        if (body.containsKey("description") && body.containsKey("users") && body.containsKey("subtasks")){
            String description = body.get("description");
            ObjectMapper objectMapper = new ObjectMapper();
            Set<User> users = objectMapper.readValue(body.get("users"), new TypeReference<Set<User>>() {});
            Set<Subtask> subtasks = objectMapper.readValue(body.get("subtasks"), new TypeReference<Set<Subtask>>() {});
            task = taskService.createTask(name,description,team,users,subtasks);
        }
        else{
            task = taskService.createTask(name,team);
        }
        User user = (User)authentication.getPrincipal();
        user.getTasks().add(task);
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Task created!");
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
            return ResponseEntity.ok(tasks);
        }
        else if(myRole.equals(PublicVariables.UserRole.MEMBER)){
            Set<TaskMemberDTO> tasks = new HashSet<>();
            for (Task task : ((User) authentication.getPrincipal()).getTasks()){
                tasks.add(new TaskMemberDTO(task));
            }
            return ResponseEntity.ok(tasks);
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
            return ResponseEntity.ok(new TaskManagerDTO(task));
        }
        else if(task.getUsers().contains((User)authentication.getPrincipal())){
            return ResponseEntity.ok(new TaskMemberDTO(task));
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PutMapping("/tasks/{ID}")
    public ResponseEntity<?> putTask(@PathVariable long teamID,@PathVariable long ID, @RequestBody Map <String, Object> body, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(ID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);

        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER) || task.getUsers().contains((User)authentication.getPrincipal())){
            task.setName(body.get("name").toString());
            task.setDescription(body.get("description").toString());
            task.setTaskStatus(PublicVariables.TaskStatus.fromString(body.get("taskStatus").toString()));
            Set<Long> userIDs = (Set<Long>)body.get("users");
            Set<User> users = userIDs.stream().map(id -> userService.getUserByID(id)).collect(Collectors.toSet());
            task.setUsers(users);
            Set<Long> subtaskIDs = (Set<Long>)body.get("subtasks");
            Set<Subtask> subtasks = subtaskIDs.stream().map(id -> subtaskService.getSubtaskByID(task.getId(), id)).collect(Collectors.toSet());
            task.setSubtasks(subtasks);
            taskService.saveTask(task);
            return ResponseEntity.ok("Task fully changed");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

    @PatchMapping("/tasks/{ID}")
    public ResponseEntity<?> patchTask(@PathVariable long teamID,@PathVariable long ID, @RequestBody Map<String,Object> body, Authentication authentication){
        Team team = teamService.getTeamByID(teamID);
        Task task = taskService.getTaskByID(ID,team);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole((User)authentication.getPrincipal(),team);

        if (myRole.equals(PublicVariables.UserRole.ADMIN) || myRole.equals(PublicVariables.UserRole.MANAGER) || task.getUsers().contains((User)authentication.getPrincipal())){
            if (body.containsKey("id") || body.containsKey("creationDate")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't patch sensitive fields!");
            }
            if (body.containsKey("name")){
                task.setName(body.get("name").toString());
            }
            if (body.containsKey("description")){
                task.setDescription(body.get("description").toString());
            }
            if (body.containsKey("taskStatus")){
                task.setTaskStatus(PublicVariables.TaskStatus.fromString(body.get("taskStatus").toString()));
            }
            //TODO: Have to think about it, we don't want users deleting their managers or admins
            if (body.containsKey("users")){
                Set<Long> userIDs = (Set<Long>)body.get("users");
                Set<User> users = userIDs.stream().map(id -> userService.getUserByID(id)).collect(Collectors.toSet());
                task.setUsers(users);
            }
            if (body.containsKey("subtasks")){
                Set<Long> subtaskIDs = (Set<Long>)body.get("subtasks");
                Set<Subtask> subtasks = subtaskIDs.stream().map(id -> subtaskService.getSubtaskByID(task.getId(), id)).collect(Collectors.toSet());
                task.setSubtasks(subtasks);
            }
            taskService.saveTask(task);
            return ResponseEntity.ok("Task updated");
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
            return ResponseEntity.ok("Task deleted");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(MenuOptions.NoPermissionsMessage());
        }
    }

}
