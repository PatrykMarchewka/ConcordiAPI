package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.*;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.*;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.Subtasks.SubtaskService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final SubtaskService subtaskService;
    private final TeamService teamService;
    private final UserService userService;
    private final RoleRegistry roleRegistry;

    @Autowired
    public TaskService(TaskRepository taskRepository, SubtaskService subtaskService, @Lazy TeamService teamService, UserService userService, RoleRegistry roleRegistry){
        this.taskRepository = taskRepository;
        this.subtaskService = subtaskService;
        this.teamService = teamService;
        this.userService = userService;
        this.roleRegistry = roleRegistry;
    }

    final List<TaskUpdater> updaters(){
        return List.of(new TaskNameUpdater(),
                new TaskDescriptionUpdater(),
                new TaskTeamUpdater(teamService),
                new TaskStatusUpdater(),
                new TaskUserUpdater(userService,this),
                new TaskSubtaskUpdater(subtaskService,this),
                new TaskCreationDateUpdater(),
                new TaskUpdateDateUpdater());
    }

    private void applyCreateUpdates(Task task, TaskRequestBody body){
        for (TaskUpdater updater : updaters()){
            if (updater instanceof TaskCREATEUpdater createUpdater){
                createUpdater.CREATEUpdate(task,body);
            }
        }
    }

    private void applyPutUpdates(Task task, TaskRequestBody body){
        for (TaskUpdater updater : updaters()){
            if (updater instanceof TaskPUTUpdater putUpdater){
                putUpdater.PUTUpdate(task,body);
            }
        }
    }

    private void applyPatchUpdates(Task task, TaskRequestBody body){
        for (TaskUpdater updater : updaters()){
            if (updater instanceof TaskPATCHUpdater patchUpdater){
                patchUpdater.PATCHUpdate(task,body);
            }
        }
    }



    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }


    public Set<Task> getAllTasks(Team team){
        return taskRepository.findByTeam(team);
    }

    /**
     * Unused, TODO: Refactor
     * @return
     */
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

    /**
     * Unused, TODO: Refactor
     * @param user
     * @return
     */
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

    /**
     * Unused, TODO: Refactor
     */
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

    /**
     * Unused, TODO: Refactor
     */

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

    @Transactional
    public Task createTask(TaskRequestBody body, Team team){
        userService.validateUsers(body.getUsers(),team);
        subtaskService.validateSubtasks(body.getSubtasks());
        Task task = new Task();
        applyCreateUpdates(task,body);
        saveTask(task);

        return task;
    }

    @Transactional
    public Task putTask(TaskRequestBody body, Team team, Task task) {
        userService.validateUsers(body.getUsers(),team);
        subtaskService.validateSubtasks(body.getSubtasks());
        applyPutUpdates(task,body);
        saveTask(task);
        return task;
    }

    @Transactional
    public Task patchTask(Task task, TaskRequestBody body, Team team){
        userService.validateUsers(body.getUsers(),team);
        subtaskService.validateSubtasks(body.getSubtasks());
        applyPatchUpdates(task, body);
        saveTask(task);
        return task;
    }


    @Transactional
    public Task deleteTaskByID(long ID, Team team){
        Task task = getTaskByIDAndTeam(ID,team);
        userService.removeTaskFromAllUsers(task);
        teamService.removeTaskFromTeam(team,task);
        return task;
    }


    @Transactional
    public Task saveTask(Task task){
        task.setUpdateDate(OffsetDateTime.now());
        return taskRepository.save(task);
    }

    public Task getTaskByIDAndTeam(long id, Team team){
        return taskRepository.findByIdAndTeam(id,team).orElseThrow(() -> new NotFoundException());
    }

    @Transactional
    public void addUserToTask(Task task, User user){
        task.getUsers().add(user);
        saveTask(task);
        userService.addTaskToUser(user,task);
    }

    @Transactional
    public void removeUserFromTask(Task task, User user){
        task.getUsers().remove(user);
        saveTask(task);
        userService.removeTaskFromUser(user,task);
    }

    @Transactional
    public void addSubtaskToTask(Task task, Subtask subtask){
        task.getSubtasks().add(subtask);
        saveTask(task);
        subtaskService.setTaskToSubtask(subtask,task);
    }

    @Transactional
    public void removeSubtaskFromTask(Task task, Subtask subtask){
        task.getSubtasks().remove(subtask);
        saveTask(task);
        subtaskService.deleteSubtask(task.getID(), subtask.getID());
    }




    public Set<TaskDTO> getAllTasksRole(PublicVariables.UserRole role, Team team, User user){
        return  roleRegistry.getAllTasksMap(user,team).getOrDefault(role,Set.of());
    }


    public Set<TaskManagerDTO> getAllTasksManager(Team team){
        Set<TaskManagerDTO> filteredTasks = new HashSet<>();
        for (Task task : getAllTasks(team)){
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

    public Set<TaskDTO> getMyTasksRole(PublicVariables.UserRole role, User user){
        return roleRegistry.getMyTasksMap(user).getOrDefault(role,Set.of());
    }

    public Set<TaskManagerDTO> getMyTasksManager(User user){
        Set<TaskManagerDTO> filteredTasks = new HashSet<>();
        for (Task task : user.getTasks()){
            filteredTasks.add(new TaskManagerDTO(task));
        }
        return filteredTasks;
    }


    public TaskDTO getInformationAboutTaskRole(PublicVariables.UserRole role, Task task, User user) {
        return roleRegistry.getInformationAboutTaskRoleMap(task,user).entrySet().stream().filter(entry -> entry.getKey().test(role)).map(entry -> entry.getValue().apply(task)).findFirst().orElseThrow(() -> new NoPrivilegesException());
    }

    public boolean putTaskRole(PublicVariables.UserRole role, Task task, User user){
        return roleRegistry.putTaskRoleMap(user).getOrDefault(role, t-> false).test(task);
    }



    // Reflection for other, maybe useful someday
//        Field[] fields = body.getClass().getDeclaredFields();
//
//        for (Field field : fields) {
//            field.setAccessible(true);
//            try {
//                Object value = field.get(body);
//                if (value != null) {
//                    Field taskField = task.getClass().getDeclaredField(field.getName());
//                    taskField.setAccessible(true);
//                    taskField.set(task, value);
//                }
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                throw new RuntimeException("Failed to update field: " + field.getName(), e);
//            }
//        }





    void removeUsersFromTask(Task task){
        for (User user : task.getUsers()){
            removeUserFromTask(task, user);
        }
    }

    private void removeSubtasksFromTask(Task task){
        for (Subtask subtask : task.getSubtasks()){
            removeSubtaskFromTask(task,subtask);
        }
    }

    void addUsersToTask(Task task, Set<User> users){
        for (User user : users){
            addUserToTask(task,user);
        }
    }

}
