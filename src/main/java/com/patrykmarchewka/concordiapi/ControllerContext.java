package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import org.springframework.security.core.Authentication;

public class ControllerContext {
    private final User user;
    private final Team team;
    private Task task;
    private final PublicVariables.UserRole userRole;
    private PublicVariables.UserRole otherRole;

    private ControllerContext(User user, Team team, PublicVariables.UserRole userRole, Task task) {
        this.user = user;
        this.team = team;
        this.userRole = userRole;
        this.task = task;
    }

    private ControllerContext(User user, Team team, PublicVariables.UserRole userRole) {
        this.user = user;
        this.team = team;
        this.userRole = userRole;
    }

    private ControllerContext(User user, Team team, PublicVariables.UserRole myRole, PublicVariables.UserRole otherRole){
        this.user = user;
        this.team = team;
        this.userRole = myRole;
        this.otherRole = otherRole;
    }

    public static ControllerContext forSubtasks(Authentication authentication, long teamID, long taskID, TeamService teamService, TaskService taskService, TeamUserRoleService teamUserRoleService) {
        User user = (User)authentication.getPrincipal();
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole userRole = teamUserRoleService.getRole(user,team);
        Task task = taskService.getTaskbyIDAndTeam(taskID,team);

        return new ControllerContext(user,team,userRole,task);
    }

    public static ControllerContext forTasks(Authentication authentication, long teamID, TeamService teamService, TeamUserRoleService teamUserRoleService){
        User user = (User)authentication.getPrincipal();
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole userRole = teamUserRoleService.getRole(user,team);

        return new ControllerContext(user,team,userRole);
    }

    public static ControllerContext forTasksWithUser(Authentication authentication, long teamID, TeamService teamService, TeamUserRoleService teamUserRoleService, User otherUser){
        User user = (User)authentication.getPrincipal();
        Team team = teamService.getTeamByID(teamID);
        PublicVariables.UserRole myRole = teamUserRoleService.getRole(user,team);
        PublicVariables.UserRole otherRole = teamUserRoleService.getRole(otherUser,team);
        return new ControllerContext(user,team,myRole,otherRole);
    }


    public User getUser() {return user;}
    public Team getTeam() {return team;}
    public Task getTask() {return task;}
    public PublicVariables.UserRole getUserRole() {return userRole;}
    public PublicVariables.UserRole getOtherRole() {return otherRole;}
}
