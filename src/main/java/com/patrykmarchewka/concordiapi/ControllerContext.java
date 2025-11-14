package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ImpossibleStateException;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component
public class ControllerContext {
    private User user;
    private Team team;
    private Task task;
    private UserRole userRole;
    private UserRole otherRole;



    private final UserService userService;
    private final TeamService teamService;
    private final TaskService taskService;
    private final TeamUserRoleService teamUserRoleService;


    @Autowired
    public ControllerContext(UserService userService, TeamService teamService, TaskService taskService, TeamUserRoleService teamUserRoleService) {
        this.userService = userService;
        this.teamService = teamService;
        this.taskService = taskService;
        this.teamUserRoleService = teamUserRoleService;
    }

    /**
     * @param authentication Authentication from logged user
     * @return User
     */
    public ControllerContext withUser(Authentication authentication){
        this.user = (User)authentication.getPrincipal();
        return this;
    }

    /**
     *
     * @param authentication Authentication from logged user
     * @return User with Teams
     */
    public ControllerContext withUserWithTeams(final Authentication authentication){
        long userID = ((User)authentication.getPrincipal()).getID();
        this.user = (User) userService.getUserWithTeamRolesAndTeams(userID);
        return this;
    }

    /**
     *
     * @param authentication Authentication from logged user
     * @return User with all foreign tables
     */
    public ControllerContext withUserFull(final Authentication authentication){
        long userID = ((User)authentication.getPrincipal()).getID();
        this.user = (User) userService.getUserFull(userID);
        return this;
    }

    /**
     *
     * @param teamID ID of the team to check for
     * @return Team
     */
    public ControllerContext withTeam(final long teamID){
        this.team = (Team) teamService.getTeamByID(teamID);
        return this;
    }

    public ControllerContext withTeamWithUserRoles(final long teamID){
        this.team = (Team) teamService.getTeamWithUserRoles(teamID);
        return this;
    }

    public ControllerContext withTeamWithUserRolesAndTasks(final long teamID){
        this.team = (Team) teamService.getTeamWithUserRolesAndTasksByID(teamID);
        return this;
    }

    /**
     *
     * @param teamID ID of the team to check for
     * @return Team with all foreign tables
     */
    public ControllerContext withTeamFull(final long teamID){
        this.team = (Team) teamService.getTeamFull(teamID);
        return this;
    }

    /**
     * Requires withTeam to be called before
     * @param taskID ID of the task to check for
     * @return Task with UserTasks
     * @throws ImpossibleStateException Thrown when called before {@link #withTeam(long)}
     */
    public ControllerContext withTaskWithUserTasks(long taskID){
       resolveTaskWithUserTasks(null, taskID);
        return this;
    }

    /**
     *
     * @param teamID ID of the team to check for
     * @param taskID ID of the task to check for
     * @return Task with UserTasks
     */
    public ControllerContext withTaskWithUserTasks(long teamID, long taskID){
        resolveTaskWithUserTasks(teamID, taskID);
        return this;
    }

    private void resolveTaskWithUserTasks(Long teamID, long taskID){
        Long resolvedTeamID = (teamID != null) ? teamID : (team != null) ? team.getID() : null;

        if (resolvedTeamID == null){
            throw new ImpossibleStateException("Cannot resolve team for withTaskWithUserTasks");
        }

        this.task = (Task) taskService.getTaskWithUserTasksByIDAndTeamID(taskID, resolvedTeamID);
    }

    /**
     * Requires withTeam to be called before
     * @param taskID ID of the task to check for
     * @return Task with Subtasks
     * @throws ImpossibleStateException Thrown when called before {@link #withTeam(long)}
     */
    public ControllerContext withTaskWithSubtasks(long taskID){
        resolveTaskWithSubtasks(null, taskID);
        return this;
    }

    /**
     *
     * @param teamID ID of the team to check for
     * @param taskID ID of the task to check for
     * @return Task with Subtasks
     */
    public ControllerContext withTaskWithSubtasks(long teamID, long taskID){
        resolveTaskWithSubtasks(teamID, taskID);
        return this;
    }

    private void resolveTaskWithSubtasks(Long teamID, long taskID){
        Long resolvedTeamID = (teamID != null) ? teamID : (team != null) ? team.getID() : null;

        if (resolvedTeamID == null){
            throw new ImpossibleStateException("Cannot resolve team for withTaskWithSubtasks");
        }

        this.task = (Task) taskService.getTaskWithSubtasksByIDAndTeamID(taskID, resolvedTeamID);
    }

    /**
     * Requires withTeam to be called before
     * @param taskID ID of the task to check for
     * @return Task with all foreign tables
     * @throws ImpossibleStateException Thrown when called before {@link #withTeam(long)}
     */
    public ControllerContext withTaskFull(long taskID){
        resolveTaskFull(null, taskID);
        return this;
    }

    /**
     *
     * @param teamID ID of the team to check for
     * @param taskID ID of the task to check for
     * @return Task with all foreign tables
     */
    public ControllerContext withTaskFull(long teamID, long taskID){
        resolveTaskFull(teamID, taskID);
        return this;
    }

    private void resolveTaskFull(Long teamID, long taskID){
        Long resolvedTeamID = (teamID != null) ? teamID : (team != null) ? team.getID() : null;

        if (resolvedTeamID == null){
            throw new ImpossibleStateException("Cannot resolve team for withTaskFull");
        }

        this.task = (Task) taskService.getTaskFullByIDAndTeamID(taskID, resolvedTeamID);
    }

    /**
     * Requires withUser and withTeam to be called before
     * @return UserRole
     * @throws ImpossibleStateException Thrown when called before {@link #withTeam(long)} and {@link #withUser(Authentication)}
     */
    public ControllerContext withRole(){
        resolveRole(null);
        return this;
    }

    /**
     * Requires withUser to be called before
     * @param teamID ID of the Team to check in
     * @return UserRole of the user in the provided Team
     * @throws ImpossibleStateException Thrown when called before {@link #withUser(Authentication)}
     */
    public ControllerContext withRole(long teamID){
        resolveRole(teamID);
        return this;
    }

    private void resolveRole(Long teamID){
        Long resolvedTeamID = (teamID != null) ? teamID : (team != null) ? team.getID() : null;

        if (resolvedTeamID == null){
            throw new ImpossibleStateException("Cannot resolve team for withRole");
        }

        this.userRole = teamUserRoleService.getRole(user.getID(), resolvedTeamID);
    }

    /**
     * Requires withTeam to be called before
     * @param userID ID of User to get UserRole
     * @return UserRole
     * @throws ImpossibleStateException Thrown when called before {@link #withTeam(long)}
     */
    public ControllerContext withOtherRole(long userID){
        resolveOtherRole(null, userID);
        return this;
    }

    /**
     *
     * @param userID ID of User to check
     * @param teamID ID of Team to check in
     * @return UserRole
     */
    public ControllerContext withOtherRole(long userID, long teamID){
        resolveOtherRole(teamID, userID);
        return this;
    }

    private void resolveOtherRole(Long teamID, long userID){
        Long resolvedTeamID = (teamID != null) ? teamID : (team != null) ? team.getID() : null;
        if (resolvedTeamID == null){
            throw new ImpossibleStateException("Cannot resolve team for withOtherRole");
        }

        this.otherRole = teamUserRoleService.getRole(userID,resolvedTeamID);
    }


    public User getUser() {return user;}
    public Team getTeam() {return team;}
    public Task getTask() {return task;}
    public UserRole getUserRole() {return userRole;}
    public UserRole getOtherRole() {return otherRole;}

}
