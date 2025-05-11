package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import org.springframework.security.core.Authentication;

public class ControllerContext {
    private final User user;
    private final Team team;
    private final Task task;
    private final PublicVariables.UserRole userRole;

    private ControllerContext(User user, Team team, PublicVariables.UserRole userRole, Task task) {
        this.user = user;
        this.team = team;
        this.userRole = userRole;
        this.task = task;
    }

    public static ControllerContext forSubtasks(Authentication authentication, long teamID, long taskID, TeamService teamService, TaskService taskService, TeamUserRoleService teamUserRoleService) {
        User user = (User)authentication.getPrincipal();
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole userRole = teamUserRoleService.getRole(user,team);
        Task task = taskService.getTaskByID(taskID,team);

        return new ControllerContext(user,team,userRole,task);
    }

    public static ControllerContext forTasks(Authentication authentication, long teamID, TeamService teamService, TeamUserRoleService teamUserRoleService){
        User user = (User)authentication.getPrincipal();
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole userRole = teamUserRoleService.getRole(user,team);

        return new ControllerContext(user,team,userRole,null);
    }


    public User getUser() {return user;}
    public Team getTeam() {return team;}
    public Task getTask() {return task;}
    public PublicVariables.UserRole getUserRole() {return userRole;}
}
