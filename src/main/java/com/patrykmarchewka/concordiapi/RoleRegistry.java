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
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
     * Returns Set of Tasks and TaskDTO class based on User Role
     * @param user User calling the method
     * @param team Team in which to check
     * @return Set of all tasks in team or tasks assigned to user and DTO class to specify what information to share
     */
    public Map<UserRole, Pair<Set<Task>,Class<? extends TaskDTO>>> getAllTasksInTeamMap(User user, Team team){
        Map<UserRole, Pair<Set<Task>,Class<? extends TaskDTO>>> roleActions = new HashMap<>();

        roleActions.put(UserRole.OWNER, new Pair<>(taskService.getAllTasks(team), TaskManagerDTO.class));
        roleActions.put(UserRole.ADMIN, new Pair<>(taskService.getAllTasks(team),TaskManagerDTO.class));
        roleActions.put(UserRole.MANAGER, new Pair<>(taskService.getAllTasks(team),TaskManagerDTO.class));
        roleActions.put(UserRole.MEMBER, new Pair<>(taskService.getAllTasksForUser(user, team), TaskMemberDTO.class));
        return roleActions;
    }

    /**
     * Returns DTO class to specify what information to share about tasks based on UserRole
     * @return
     */
    public Map<UserRole, Class<? extends TaskDTO>> getMyTasksMap(){
        Map<UserRole, Class<? extends TaskDTO>> roleActions = new HashMap<>();

        roleActions.put(UserRole.OWNER, TaskManagerDTO.class);
        roleActions.put(UserRole.ADMIN, TaskManagerDTO.class);
        roleActions.put(UserRole.MANAGER, TaskManagerDTO.class);
        roleActions.put(UserRole.MEMBER, TaskMemberDTO.class);

        return roleActions;
    }


}
