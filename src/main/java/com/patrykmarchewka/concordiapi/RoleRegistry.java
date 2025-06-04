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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Component
public class RoleRegistry {

    private final TaskService taskService;
    private final TeamUserRoleService teamUserRoleService;

    @Autowired
    public RoleRegistry(@Lazy TaskService taskService, TeamUserRoleService teamUserRoleService){
        this.taskService = taskService;
        this.teamUserRoleService = teamUserRoleService;
    }


    //Team

    /**
     * Map of user Roles and DTOs to return
     * @return Appropriate DTO for the UserRole
     */
    public Map<UserRole, BiFunction<Team, User, TeamDTO>> createTeamDTOMap() {
        return Map.of(
            UserRole.OWNER, (team, user) -> new TeamAdminDTO(team, teamUserRoleService),
            UserRole.ADMIN, (team, user) -> new TeamAdminDTO(team, teamUserRoleService),
            UserRole.MANAGER, (team, user) -> new TeamManagerDTO(team, teamUserRoleService),
            UserRole.MEMBER, (team, user) -> new TeamMemberDTO(team, user, teamUserRoleService)
    );
}


    //Users

    /**
     * Map of User Roles and Set of User to return
     * @param team Current team in which to search
     * @param role Role to search for
     * @return Set of Users with that role in the team
     */
    public Map<UserRole, Supplier<Set<User>>> createUserDTOMapWithParam(Team team, UserRole role){
        return Map.of(
                UserRole.OWNER, () -> teamUserRoleService.getAllByTeamAndUserRole(team, role),
                UserRole.ADMIN, () -> teamUserRoleService.getAllByTeamAndUserRole(team, role),
                UserRole.MANAGER, () -> teamUserRoleService.getAllByTeamAndUserRole(team, role)
        );
    }

    /**
     * Map of User Roles and Set of User to return
     * @param team Current team in which to search
     * @return Set of users in the team
     */
    public Map<UserRole, Supplier<Set<User>>> createUserDTOMapNoParam(Team team){
        return Map.of(
                UserRole.OWNER, () -> team.getTeammates(),
                UserRole.ADMIN, () -> team.getTeammates(),
                UserRole.MANAGER, () -> team.getTeammates()
        );
    }


    //Tasks

    /**
     * Map of User and whether user can PUT task
     * @param user User to check permissions
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
     * Map of User Role and Set of Task DTO to return based on User Role
     * @param user User to check permissions
     * @return Set of Task DTO that user is assigned to
     */
    public Map<UserRole, Set<TaskDTO>> getMyTasksMap(User user) {
        Map<UserRole, Set<TaskDTO>> roleActions = new HashMap<>();

        roleActions.put(UserRole.OWNER, new HashSet<>(taskService.getMyTasksManager(user)));
        roleActions.put(UserRole.ADMIN, new HashSet<>(taskService.getMyTasksManager(user)));
        roleActions.put(UserRole.MANAGER, new HashSet<>(taskService.getMyTasksManager(user)));
        roleActions.put(UserRole.MEMBER, new HashSet<>(taskService.getAllTasksMember(user)));

        return roleActions;
    }

    /**
     * Map of User Role and Set of Task DTO to return based on User Role
     * @param user User to check permissions
     * @param team Team to which return tasks of
     * @return Set of Task DTO in the team
     */
    public Map<UserRole, Set<TaskDTO>> getAllTasksMap(User user, Team team) {
        Map<UserRole,Set<TaskDTO>> roleActions = new HashMap<>();

        roleActions.put(UserRole.OWNER, new HashSet<>(taskService.getAllTasksManager(team)));
        roleActions.put(UserRole.ADMIN, new HashSet<>(taskService.getAllTasksManager(team)));
        roleActions.put(UserRole.MANAGER, new HashSet<>(taskService.getAllTasksManager(team)));
        roleActions.put(UserRole.MEMBER, new HashSet<>(taskService.getAllTasksMember(user)));

        return roleActions;
}


}
