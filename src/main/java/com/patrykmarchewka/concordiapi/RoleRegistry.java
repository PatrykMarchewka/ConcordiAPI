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
    public Map<UserRole, BiFunction<Team, User, TeamDTO>> createTeamDTOMap() {
        return Map.of(
            UserRole.OWNER, (team, user) -> new TeamAdminDTO(team, teamUserRoleService),
            UserRole.ADMIN, (team, user) -> new TeamAdminDTO(team, teamUserRoleService),
            UserRole.MANAGER, (team, user) -> new TeamManagerDTO(team, teamUserRoleService),
            UserRole.MEMBER, (team, user) -> new TeamMemberDTO(team, user, teamUserRoleService)
    );
}


    //Users
    public Map<UserRole, Supplier<Set<User>>> createUserDTOMapWithParam(Team team, UserRole role){
        return Map.of(
                UserRole.OWNER, () -> teamUserRoleService.getAllByTeamAndUserRole(team, role),
                UserRole.ADMIN, () -> teamUserRoleService.getAllByTeamAndUserRole(team, role),
                UserRole.MANAGER, () -> teamUserRoleService.getAllByTeamAndUserRole(team, role)
        );
    }

    public Map<UserRole, Supplier<Set<User>>> createUserDTOMapNoParam(Team team){
        return Map.of(
                UserRole.OWNER, () -> team.getTeammates(),
                UserRole.ADMIN, () -> team.getTeammates(),
                UserRole.MANAGER, () -> team.getTeammates()
        );
    }





    //Tasks
    public Map<UserRole, Predicate<Task>> putTaskRoleMap(User user) {
        return Map.of(
                UserRole.OWNER, t -> true,
                UserRole.ADMIN, t -> true,
                UserRole.MANAGER, t -> true,
                UserRole.MEMBER, t -> t.hasUser(user)
        );
    }



    public Map<Predicate<UserRole>, Function<Task, TaskDTO>> getInformationAboutTaskRoleMap(Task task, User user) {
        return Map.of(
                u -> u.isAdminGroup(), t -> new TaskManagerDTO(t),
                u -> task.hasUser(user), t -> new TaskMemberDTO(t)
        );
    }

    public Map<UserRole, Set<TaskDTO>> getMyTasksMap(User user) {
        Map<UserRole, Set<TaskDTO>> roleActions = new HashMap<>();

        roleActions.put(UserRole.OWNER, new HashSet<>(taskService.getMyTasksManager(user)));
        roleActions.put(UserRole.ADMIN, new HashSet<>(taskService.getMyTasksManager(user)));
        roleActions.put(UserRole.MANAGER, new HashSet<>(taskService.getMyTasksManager(user)));
        roleActions.put(UserRole.MEMBER, new HashSet<>(taskService.getAllTasksMember(user)));

        return roleActions;
    }

    public Map<UserRole, Set<TaskDTO>> getAllTasksMap(User user, Team team) {
        Map<UserRole,Set<TaskDTO>> roleActions = new HashMap<>();

        roleActions.put(UserRole.OWNER, new HashSet<>(taskService.getAllTasksManager(team)));
        roleActions.put(UserRole.ADMIN, new HashSet<>(taskService.getAllTasksManager(team)));
        roleActions.put(UserRole.MANAGER, new HashSet<>(taskService.getAllTasksManager(team)));
        roleActions.put(UserRole.MEMBER, new HashSet<>(taskService.getAllTasksMember(user)));

        return roleActions;
}


}
