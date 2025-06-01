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

    public RoleRegistry(@Lazy TaskService taskService, TeamUserRoleService teamUserRoleService){
        this.taskService = taskService;
        this.teamUserRoleService = teamUserRoleService;
    }


    //Team
    public Map<PublicVariables.UserRole, BiFunction<Team, User, TeamDTO>> createTeamDTOMap() {
        return Map.of(
            PublicVariables.UserRole.OWNER, (team, user) -> new TeamAdminDTO(team, teamUserRoleService),
            PublicVariables.UserRole.ADMIN, (team, user) -> new TeamAdminDTO(team, teamUserRoleService),
            PublicVariables.UserRole.MANAGER, (team, user) -> new TeamManagerDTO(team, teamUserRoleService),
            PublicVariables.UserRole.MEMBER, (team, user) -> new TeamMemberDTO(team, user, teamUserRoleService)
    );
}


    //Users
    public Map<PublicVariables.UserRole, Supplier<Set<User>>> createUserDTOMapWithParam(Team team, PublicVariables.UserRole role){
        return Map.of(
                PublicVariables.UserRole.OWNER, () -> teamUserRoleService.getAllByTeamAndUserRole(team, role),
                PublicVariables.UserRole.ADMIN, () -> teamUserRoleService.getAllByTeamAndUserRole(team, role),
                PublicVariables.UserRole.MANAGER, () -> teamUserRoleService.getAllByTeamAndUserRole(team, role)
        );
    }

    public Map<PublicVariables.UserRole, Supplier<Set<User>>> createUserDTOMapNoParam(Team team){
        return Map.of(
                PublicVariables.UserRole.OWNER, () -> team.getTeammates(),
                PublicVariables.UserRole.ADMIN, () -> team.getTeammates(),
                PublicVariables.UserRole.MANAGER, () -> team.getTeammates()
        );
    }





    //Tasks
    public Map<PublicVariables.UserRole, Predicate<Task>> putTaskRoleMap(User user) {
        return Map.of(
                PublicVariables.UserRole.OWNER, t -> true,
                PublicVariables.UserRole.ADMIN, t -> true,
                PublicVariables.UserRole.MANAGER, t -> true,
                PublicVariables.UserRole.MEMBER, t -> t.hasUser(user)
        );
    }



    public Map<Predicate<PublicVariables.UserRole>, Function<Task, TaskDTO>> getInformationAboutTaskRoleMap(Task task, User user) {
        return Map.of(
                u -> u.isAdminGroup(), t -> new TaskManagerDTO(t),
                u -> task.hasUser(user), t -> new TaskMemberDTO(t)
        );
    }

    public Map<PublicVariables.UserRole, Set<TaskDTO>> getMyTasksMap(User user) {
        Map<PublicVariables.UserRole, Set<TaskDTO>> roleActions = new HashMap<>();

        roleActions.put(PublicVariables.UserRole.OWNER, new HashSet<>(taskService.getMyTasksManager(user)));
        roleActions.put(PublicVariables.UserRole.ADMIN, new HashSet<>(taskService.getMyTasksManager(user)));
        roleActions.put(PublicVariables.UserRole.MANAGER, new HashSet<>(taskService.getMyTasksManager(user)));
        roleActions.put(PublicVariables.UserRole.MEMBER, new HashSet<>(taskService.getAllTasksMember(user)));

        return roleActions;
    }

    public Map<PublicVariables.UserRole, Set<TaskDTO>> getAllTasksMap(User user, Team team) {
        Map<PublicVariables.UserRole,Set<TaskDTO>> roleActions = new HashMap<>();

        roleActions.put(PublicVariables.UserRole.OWNER, new HashSet<>(taskService.getAllTasksManager(team)));
        roleActions.put(PublicVariables.UserRole.ADMIN, new HashSet<>(taskService.getAllTasksManager(team)));
        roleActions.put(PublicVariables.UserRole.MANAGER, new HashSet<>(taskService.getAllTasksManager(team)));
        roleActions.put(PublicVariables.UserRole.MEMBER, new HashSet<>(taskService.getAllTasksMember(user)));

        return roleActions;
}


}
