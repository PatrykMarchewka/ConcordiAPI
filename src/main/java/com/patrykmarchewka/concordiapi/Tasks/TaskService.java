package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.*;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.*;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.Subtasks.SubtaskService;
import com.patrykmarchewka.concordiapi.Tasks.Updaters.TaskUpdatersService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final SubtaskService subtaskService;
    private final TeamService teamService;
    private final UserService userService;
    private final RoleRegistry roleRegistry;
    private final TaskUpdatersService taskUpdatersService;

    @Autowired
    public TaskService(TaskRepository taskRepository, SubtaskService subtaskService, @Lazy TeamService teamService, UserService userService, RoleRegistry roleRegistry, TaskUpdatersService taskUpdatersService){
        this.taskRepository = taskRepository;
        this.subtaskService = subtaskService;
        this.teamService = teamService;
        this.userService = userService;
        this.roleRegistry = roleRegistry;
        this.taskUpdatersService = taskUpdatersService;
    }



    /**
     * Returns all tasks in given team
     * @param team Team to chose to get tasks from
     * @return Set of tasks in given Team
     */
    public Set<Task> getAllTasks(Team team){
        return taskRepository.findByTeam(team);
    }

    /**
     * Unused, returns all tasks that don't have any users assigned
     * @param team Team in which to search
     * @return Set of tasks that have no users assigned to them
     */
    @Transactional(readOnly = true)
    public Set<Task> getAllTasksWithoutUsers(Team team){
        return getAllTasks(team).stream().filter(task -> task.getUsers().isEmpty()).collect(Collectors.toSet());
    }

    /**
     * Unused, returns all tasks that are assigned to user in given team
     * @param user User to search for
     * @param team Team in which to search
     * @return Set of tasks that user is assigned to
     */
    @Transactional(readOnly = true)
    public Set<Task> getAllTasksForUser(User user, Team team){
        return getAllTasks(team).stream().filter(task -> task.hasUser(user)).collect(Collectors.toSet());
    }

    /**
     * Unused, returns all tasks with given task status in a team
     * @param status TaskStatus to check for
     * @param team Team in which to search
     * @return Set of tasks that have given TaskStatus
     */
    @Transactional(readOnly = true)
    public Set<Task> getAllTasksByStatus(TaskStatus status, Team team){
        return getAllTasks(team).stream().filter(task -> task.getTaskStatus().equals(status)).collect(Collectors.toSet());
    }

    /**
     * Returns all tasks that didn't had any update in given number of days, should only be called from {@link #getInactiveTasks(Team, UserRole, Integer)}
     * @param days Minimum number of days to search for
     * @param team Team in which to search for
     * @return Set of tasks that weren't updated in days
     * @throws IllegalArgumentException thrown when number is set to zero or less
     */
    @Transactional(readOnly = true)
    public Set<Task> getAllTasksNoUpdatesIn(int days, Team team){
        if(days <= 0){
            throw new IllegalArgumentException("Number of days cannot be zero or negative!");
        }
        return getAllTasks(team).stream().filter( task -> ChronoUnit.DAYS.between(task.getUpdateDate(), OffsetDateTime.now()) > days).collect(Collectors.toSet());
    }

    /**
     * Creates task with given body details
     * @param body TaskRequestBody with details of task to create
     * @param team Team in which to make the task
     * @return Created task
     */
    @Transactional
    public Task createTask(TaskRequestBody body, Team team){
        userService.validateUsersForTasks(body.getUsers(),team);
        subtaskService.validateSubtasks(body.getSubtasks());
        Task task = new Task();
        taskUpdatersService.update(task,body,UpdateType.CREATE);
        saveTask(task);

        return task;
    }

    /**
     * Edits task completely with all values
     * @param body TaskRequestBody with new values
     * @param team Team in which task exists
     * @param task Task to edit
     * @return Edited task
     */
    @Transactional
    public Task putTask(TaskRequestBody body, Team team, Task task) {
        userService.validateUsersForTasks(body.getUsers(),team);
        subtaskService.validateSubtasks(body.getSubtasks());
        taskUpdatersService.update(task,body,UpdateType.PUT);
        saveTask(task);
        return task;
    }

    /**
     * Edits partially task with new values from body
     * @param task Task to edit
     * @param body TeamRequestBody with new updated values
     * @param team Team in which task exists
     * @return Edited task
     */
    @Transactional
    public Task patchTask(Task task, TaskRequestBody body, Team team){
        userService.validateUsersForTasks(body.getUsers(),team);
        subtaskService.validateSubtasks(body.getSubtasks());
        taskUpdatersService.update(task,body,UpdateType.PATCH);
        saveTask(task);
        return task;
    }


    /**
     * Deletes task with the specified ID
     * @param ID ID of the task to delete
     * @param team Team in which to delete
     * @return Deleted task
     */
    @Transactional
    public Task deleteTaskByID(long ID, Team team){
        Task task = getTaskByIDAndTeam(ID,team);
        userService.removeTaskFromAllUsers(task);
        teamService.removeTaskFromTeam(team,task);
        return task;
    }

    /**
     * Saves pending changes to the task
     * @param task Task to save
     * @return Saved task
     */
    @Transactional
    public Task saveTask(Task task){
        task.setUpdateDate(OffsetDateTime.now());
        return taskRepository.save(task);
    }

    /**
     * Returns task with given ID and team
     * @param id ID of the task to search for
     * @param team Team in which task is located
     * @return Task with given ID
     * @throws NotFoundException Thrown when can't find task with specified ID and Team
     */
    public Task getTaskByIDAndTeam(long id, Team team){
        return taskRepository.findByIdAndTeam(id,team).orElseThrow(() -> new NotFoundException());
    }

    /**
     * Adds user to Task
     * @param task Task to attach to User
     * @param user User to attach to Task
     */
    @Transactional
    public void addUserToTask(Task task, User user){
        task.addUser(user);
        saveTask(task);
        userService.addTaskToUser(user,task);
    }

    /**
     * Removes user from Task
     * @param task Task to edit
     * @param user User to remove from Task
     */
    @Transactional
    public void removeUserFromTask(Task task, User user){
        task.removeUser(user);
        saveTask(task);
        userService.removeTaskFromUser(user,task);
    }

    /**
     * Adds subtask to Task
     * @param task Task to edit
     * @param subtask Subtask to add
     */
    @Transactional
    public void addSubtaskToTask(Task task, Subtask subtask){
        task.addSubtask(subtask);
        saveTask(task);
        subtaskService.setTaskToSubtask(subtask,task);
    }

    /**
     * Removes subtask from Task but doesn't delete it. If you want to delete the subtask check {@link #removeSubtaskFromTaskAndDelete(Task, Subtask)}
     * @param task Task to edit
     * @param subtask Subtask to remove
     */
    @Transactional
    public void removeSubtaskFromTask(Task task, Subtask subtask){
        task.removeSubtask(subtask);
        saveTask(task);
    }

    /**
     * Removes subtask from Task and deletes the subtask, if you don't want to delete the subtask check {@link #removeSubtaskFromTask(Task, Subtask)}
     * @param task Task to edit
     * @param subtask Subtask to remove and delete
     */
    @Transactional
    public void removeSubtaskFromTaskAndDelete(Task task, Subtask subtask){
        task.getSubtasks().remove(subtask);
        saveTask(task);
        subtaskService.deleteSubtask(subtask);
    }

    /**
     * Returns TaskDTO of either all tasks in team or all tasks in team assigned to user based on UserRole
     * @param user User performing the check
     * @param team Team in which to check for
     * @param role Role of the user in a team
     * @return Set of TaskDTO with information about the tasks
     * @throws RuntimeException Thrown when Task couldn't get converted into proper DTO object
     */
    public Set<TaskDTO> getAllTasks(User user, Team team, UserRole role){
        Pair<Set<Task>, Class<? extends TaskDTO>> pair = roleRegistry.getAllTasksInTeamMap(user, team).get(role);

        return pair.getFirst().stream().map(task -> {
            try {
                return pair.getSecond().getDeclaredConstructor(task.getClass()).newInstance(task);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
    }

    /**
     * Returns TaskDTO of all tasks in team assigned to user regardless of UserRole. <br>
     * UserRole is used to identify what kind of information to return (which DTO)
     * @param user User performing the check
     * @param team Team in which to check for
     * @param role Role of the user in a team
     * @return Set of TaskDTO with information about the tasks
     * @throws RuntimeException Thrown when Task couldn't get converted into proper DTO object
     */
    public Set<TaskDTO> getMyTasks(User user,Team team, UserRole role){
        Class<? extends TaskDTO> taskClass = roleRegistry.getMyTasksMap().get(role);
        return getAllTasksForUser(user, team).stream().map(item -> {
            try {
                return taskClass.getDeclaredConstructor(item.getClass()).newInstance(item);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
    }

    /**
     * Returns TaskDTO of all tasks that didn't had an update in at least x days
     * @param team Team in which to search the tasks
     * @param role Role of the user asking to return correct DTOs
     * @param days Minimum amount of days to be labeled as inactive
     * @return Set of TaskDTO with information about the tasks
     * @throws RuntimeException Thrown when Task couldn't get converted into proper DTO object
     */
    public Set<TaskDTO> getInactiveTasks(Team team, UserRole role, Integer days){
        Class<? extends TaskDTO> taskClass = roleRegistry.getMyTasksMap().get(role);
        return getAllTasksNoUpdatesIn(days,team).stream().map(task -> {
            try {
                return taskClass.getDeclaredConstructor(task.getClass()).newInstance(task);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
    }

    /**
     * Returns TaskDTO about given Task
     * @param role UserRole of user asking
     * @param task Task to return DTO of
     * @param user User asking for DTO
     * @return Task DTO with information about the task
     * @throws NoPrivilegesException Thrown when user cannot get the task details
     */
    public TaskDTO getInformationAboutTaskRole(UserRole role, Task task, User user) {
        return roleRegistry.getInformationAboutTaskRoleMap(task,user).entrySet().stream().filter(entry -> entry.getKey().test(role)).map(entry -> entry.getValue().apply(task)).findFirst().orElseThrow(() -> new NoPrivilegesException());
    }

    /**
     * Checks whether user can modify task using PUT command
     * @param role Role of the User asking for permission
     * @param task Task to edit
     * @param user User asking for permission
     * @return True if user can use PUT to edit task, otherwise false
     */
    public boolean putTaskRole(UserRole role, Task task, User user){
        return roleRegistry.putTaskRoleMap(user).getOrDefault(role, t-> false).test(task);
    }

    /**
     * Removes all users from Task
     * @param task Task to remove users from
     */
    public void removeUsersFromTask(Task task){
        for (User user : task.getUsers()){
            removeUserFromTask(task, user);
        }
    }

    /**
     * Unused, removes all Subtasks from Task
     * @param task Task to remove subtasks from
     */
    private void removeSubtasksFromTask(Task task){
        for (Subtask subtask : task.getSubtasks()){
            removeSubtaskFromTaskAndDelete(task,subtask);
        }
    }

    /**
     * Adds Users to Task
     * @param task Task to add users to
     * @param users Set of Users to add to task
     */
    public void addUsersToTask(Task task, Set<User> users){
        for (User user : users){
            addUserToTask(task,user);
        }
    }

}
