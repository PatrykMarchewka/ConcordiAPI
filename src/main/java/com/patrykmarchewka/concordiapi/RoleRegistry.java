package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamAdminDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
public class RoleRegistry {

    private final TaskService taskService;

    @Autowired
    public RoleRegistry(@Lazy TaskService taskService){
        this.taskService = taskService;
    }


    //Tasks

    /**
     * Map of UserRoles and whether they can PUT task
     * @param user User asking
     * @return True if user can edit task with PUT, otherwise false
     */
    public Map<UserRole, Predicate<Task>> putTaskRoleMap(User user) {
        return Map.of(
                UserRole.OWNER, t -> true,
                UserRole.ADMIN, t -> true,
                UserRole.MANAGER, t -> true,
                UserRole.MEMBER, t -> t.hasUser(user)
        );
    }

    /**
     * Map of User Role and Task DTO to return based on User Role
     * @param task Task to return DTO of
     * @param user User to check permissions
     * @return Appropriate DTO based on User Role
     */
    public Map<Predicate<UserRole>, Function<Task, TaskDTO>> getInformationAboutTaskRoleMap(Task task, User user) {
        return Map.of(
                u -> u.isAdminGroup(), t -> new TaskManagerDTO(t),
                u -> task.hasUser(user), t -> new TaskMemberDTO(t)
        );
    }

    /**
     * Map to get appropriate tasks in a team
     * @param user User calling the method
     * @param team Team in which to check
     * @return Map of UserRoles and Set of tasks privileged to see
     */
    public Map<UserRole, Set<Task>> getAllTasksMap(User user, Team team){
        return Map.of(
                UserRole.OWNER, taskService.getAllTasks(team),
                UserRole.ADMIN, taskService.getAllTasks(team),
                UserRole.MANAGER, taskService.getAllTasks(team),
                UserRole.MEMBER, taskService.getAllTasksForUser(user, team)
        );
    }

    /**
     * Map to get appropriate TaskDTO for a task
     * @return TaskDTO of Task based on UserRole
     */
    public Map<UserRole, Function<Task, TaskDTO>> getTaskDTOMap(){
        return Map.of(
                UserRole.OWNER, TaskManagerDTO::new,
                UserRole.ADMIN, TaskManagerDTO::new,
                UserRole.MANAGER, TaskManagerDTO::new,
                UserRole.MEMBER, TaskMemberDTO::new
        );
    }


}
