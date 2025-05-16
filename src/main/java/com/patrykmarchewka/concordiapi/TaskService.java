package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.*;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
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
    public Task createTask(TaskRequestBody body, Team team){
        Task task = new Task();
        if (body.getUsers() != null){
            for (int id : body.getUsers()){
                if (userService.checkIfUserExistsInATeam(userService.getUserByID((long)id), team)){
                    addUserToTask(task, userService.getUserByID((long)id));
                }
                else{
                    throw new BadRequestException("Cannot add user to this task that is not part of the team");
                }
            }
        }

        task.setTeam(team);
        task.setName(body.getName());
        task.setDescription((body.getDescription() == null ? null : body.getDescription()));
        task.setTaskStatus((body.getTaskStatus() == null ? PublicVariables.TaskStatus.NEW : body.getTaskStatus()));
        task.setCreationDate(OffsetDateTime.now());
        task.setUpdateDate(OffsetDateTime.now());
        saveTask(task);
        team.getTasks().add(task);
        teamService.saveTeam(team);

        return task;
    }

    @Transactional
    public Task putTask(TaskRequestBody body, Team team, Task task) {
        for (int id : body.getUsers()) {
            if (!userService.checkIfUserExistsInATeam(userService.getUserByID((long) id), team)) {
                throw new BadRequestException("Cannot add user to this task that is not part of the team");
            }
        }
        for (int id : body.getSubtasks()){
            if (!subtaskService.checkIfSubtaskExistsByID(id)){
                throw new BadRequestException("Tried to add subtask that doesn't exist");
            }
        }
        task.setName(body.getName());
        task.setDescription(body.getDescription());
        task.setTaskStatus(body.getTaskStatus());
        for (User user : task.getUsers()) {
            removeUserFromTask(team, task.getID(), user);
        }
        for (Integer ID : body.getUsers()){
            addUserToTask(task, userService.getUserByID((long)ID));
        }
        for (Subtask subtask : task.getSubtasks()) {
            removeSubtaskFromTask(team, task.getID(), subtask);
        }
        for (int id : body.getSubtasks()) {
            addSubtaskToTask(team, task.getID(), subtaskService.getSubtaskByID(task.getID(), id));
        }
        saveTask(task);
        return task;
    }

    @Transactional
    public Task deleteTaskByID(long ID, Team team){
        Task task = getTaskbyIDAndTeam(ID,team);
        for (User user : task.getUsers()){
            user.getTasks().remove(task);
            userService.saveUser(user);
        }
        team.getTasks().remove(task);
        teamService.saveTeam(team);
        return task;
    }


    @Transactional
    public Task saveTask(Task task){
        task.setUpdateDate(OffsetDateTime.now());
        return taskRepository.save(task);
    }

    public Task getTaskbyIDAndTeam(long id, Team team){
        return taskRepository.findByIdAndTeam(id,team).orElseThrow(() -> new NotFoundException());
    }


    @Transactional
    public void addUserToTask(Team team, long taskID, User user){
        Task task = getTaskbyIDAndTeam(taskID,team);
        task.getUsers().add(user);
        saveTask(task);
        user.getTasks().add(task);
        userRepository.save(user);
        userService.saveUser(user);
    }

    @Transactional
    public void addUserToTask(Task task, User user){
        task.getUsers().add(user);
        saveTask(task);
        user.getTasks().add(task);
        userService.saveUser(user);
    }

    @Transactional
    public void removeUserFromTask(Team team, long taskID, User user){
        Task task = getTaskbyIDAndTeam(taskID,team);
        task.getUsers().remove(user);
        saveTask(task);
        user.getTasks().remove(task);
        userService.saveUser(user);
    }

    @Transactional
    public void addSubtaskToTask(Team team, long taskID, Subtask subtask){
        Task task = getTaskbyIDAndTeam(taskID,team);
        task.getSubtasks().add(subtask);
        saveTask(task);
        subtask.setTask(task);
        subtaskService.saveSubtask(subtask);
    }

    @Transactional
    public void removeSubtaskFromTask(Team team, long taskID, Subtask subtask){
        Task task = getTaskbyIDAndTeam(taskID,team);
        task.getSubtasks().remove(subtask);
        saveTask(task);
        subtaskService.deleteSubtask(taskID, subtask.getID());
    }




    public Set<?> getAllTasksRoleController(PublicVariables.UserRole role, TaskService taskService, Team team, User user){
        Map<PublicVariables.UserRole, Supplier<Set<?>>> roleActions = Map.of(
                PublicVariables.UserRole.OWNER, () -> getAllTasksManager(taskService,team),
                PublicVariables.UserRole.ADMIN, () -> getAllTasksManager(taskService,team),
                PublicVariables.UserRole.MANAGER, () -> getAllTasksManager(taskService,team),
                PublicVariables.UserRole.MEMBER, () -> getAllTasksMember(user));

        return  roleActions.getOrDefault(role,() -> Set.of()).get();
    }


    public Set<TaskManagerDTO> getAllTasksManager(TaskService taskService, Team team){
        Set<TaskManagerDTO> filteredTasks = new HashSet<>();
        for (Task task : taskService.getAllTasks(team)){
            filteredTasks.add(new TaskManagerDTO(task));
        }
        return filteredTasks;
    }
    public Set<TaskMemberDTO> getAllTasksMember(User user){
        Set<TaskMemberDTO> filteredTasks = new HashSet<>();
        for (Task task : user.getTasks()){
            filteredTasks.add(new TaskMemberDTO(task));
        }
        return filteredTasks;
    }

    public Set<?> getMyTasksRoleController(PublicVariables.UserRole role, User user){
        Map<PublicVariables.UserRole, Supplier<Set<?>>> roleActions = Map.of(
                PublicVariables.UserRole.OWNER, () -> getMyTasksManager(user),
                PublicVariables.UserRole.ADMIN, () -> getMyTasksManager(user),
                PublicVariables.UserRole.MANAGER, () -> getMyTasksManager(user),
                PublicVariables.UserRole.MEMBER, () -> getAllTasksMember(user));

        return roleActions.getOrDefault(role, () -> Set.of()).get();
    }

    public Set<TaskManagerDTO> getMyTasksManager(User user){
        Set<TaskManagerDTO> filteredTasks = new HashSet<>();
        for (Task task : user.getTasks()){
            filteredTasks.add(new TaskManagerDTO(task));
        }
        return filteredTasks;
    }

    public Object getInformationAboutTaskRoleController(PublicVariables.UserRole role, Task task, User user) {
        Map<Predicate<PublicVariables.UserRole>, Function<Task, Object>> roleActions = Map.of(
                u -> u.isAdminGroup(), t -> new TaskManagerDTO(t),
                u -> task.hasUser(user), t -> new TaskMemberDTO(t)
        );

        return roleActions.entrySet().stream().filter(entry -> entry.getKey().test(role)).map(entry -> entry.getValue().apply(task)).findFirst().orElseThrow(() -> new NoPrivilegesException());
    }

    public boolean putTaskRoleController(PublicVariables.UserRole role, Task task, User user){
        Map<PublicVariables.UserRole, Predicate<Task>> rolePermissions = Map.of(
                PublicVariables.UserRole.OWNER, t -> true,
                PublicVariables.UserRole.ADMIN, t -> true,
                PublicVariables.UserRole.MANAGER, t -> true,
                PublicVariables.UserRole.MEMBER, t -> task.hasUser(user)

        );
        return rolePermissions.getOrDefault(role, t -> false).test(task);
    }

    @Transactional
    public Task partialUpdate(Task task, TaskRequestBody body, Team team){
        if (body.getUsers() != null){
            for (int id : body.getUsers()) {
                if (!userService.checkIfUserExistsInATeam(userService.getUserByID((long) id), team)) {
                    throw new BadRequestException("Cannot add user to this task that is not part of the team");
                }
            }
            for (User user : task.getUsers()){
                removeUserFromTask(team, task.getID(), user);
            }
            for (int id : body.getUsers()){
                if (userService.checkIfUserExistsInATeam(userService.getUserByID((long)id), team)){
                    addUserToTask(team,task.getID(), userService.getUserByID((long)id));
                }
            }

        }
        if (body.getSubtasks() != null){
            for (int id : body.getSubtasks()){
                if (!subtaskService.checkIfSubtaskExistsByID(id)){
                    throw new BadRequestException("Tried to add subtask that doesn't exist");
                }
            }
            for (Subtask subtask : task.getSubtasks()){
                removeSubtaskFromTask(team, task.getID(), subtask);
            }
            for (int id : body.getSubtasks()){
                addSubtaskToTask(team, task.getID(), subtaskService.getSubtaskByID(task.getID(), id));
            }
        }


        if (body.getName() != null){
            task.setName(body.getName());
        }
        if (body.getDescription() != null){
            task.setDescription(body.getDescription());
        }
        if (body.getTaskStatus() != null){
            task.setTaskStatus(body.getTaskStatus());
        }
        saveTask(task);
        return task;
    }



}
