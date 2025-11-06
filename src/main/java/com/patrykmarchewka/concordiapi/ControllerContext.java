package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ImpossibleStateException;
import com.patrykmarchewka.concordiapi.Invitations.InvitationService;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


/**
 * Order: (withUser, withTeam, withInvitation) -> (withTask, withRole, withOtherRole) <br>
 */
@Component
public class ControllerContext {
    private User user;
    private Team team;
    private Task task;
    private UserRole userRole;
    private UserRole otherRole;
    private Invitation invitation;



    private final UserService userService;
    private final TeamService teamService;
    private final TaskService taskService;
    private final TeamUserRoleService teamUserRoleService;
    private final InvitationService invitationService;


    @Autowired
    public ControllerContext(UserService userService, TeamService teamService, TaskService taskService, TeamUserRoleService teamUserRoleService, InvitationService invitationService) {
        this.userService = userService;
        this.teamService = teamService;
        this.taskService = taskService;
        this.teamUserRoleService = teamUserRoleService;
        this.invitationService = invitationService;
    }

    /**
     * @param authentication Authentication from logged user
     * @return User
     */
    public ControllerContext withUser(Authentication authentication){
        this.user = (User)authentication.getPrincipal();
        return this;
    }

    public ControllerContext withUserWithTeams(final Authentication authentication){
        long userID = ((User)authentication.getPrincipal()).getID();
        this.user = (User) userService.getUserWithTeamRolesAndTeams(userID);
        return this;
    }

    public ControllerContext withTeam(final long teamID){
        this.team = (Team) teamService.getTeamByID(teamID);
        return this;
    }



    public ControllerContext withTaskWithUserTasks(long taskID){
       resolveTaskWithUserTasks(null, taskID);
        return this;
    }

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
     * @return Task
     * @throws ImpossibleStateException Thrown when called before {@link #withTeam(long)}
     */
    public ControllerContext withTaskFull(long taskID){
        resolveTaskFull(null, taskID);
        return this;
    }

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
        resolveRole(null, null);
        return this;
    }

    /**
     * Requires withUser to be called before
     * @param teamID ID of the Team to check in
     * @return UserRole of the user in the provided Team
     * @throws ImpossibleStateException Thrown when called before {@link #withUser(Authentication)}
     */
    public ControllerContext withRole(long teamID){
        resolveRole(null, teamID);
        return this;
    }

    /**
     *
     * @param userID ID of User to check
     * @param teamID ID of Team to check in
     * @return UserRole
     */
    public ControllerContext withRole(long userID, long teamID){
        resolveRole(userID, teamID);
        return this;
    }

    private void resolveRole(Long userID, Long teamID){
        Long resolvedUserID = (userID != null) ? userID : (user != null) ? user.getID() : null;
        Long resolvedTeamID = (teamID != null) ? teamID : (team != null) ? team.getID() : null;

        if (resolvedUserID == null || resolvedTeamID == null){
            throw new ImpossibleStateException("Cannot resolve user/team for withRole");
        }

        this.userRole = teamUserRoleService.getRole(resolvedUserID, resolvedTeamID);
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

    public ControllerContext withOtherRole(long teamID, long userID){
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

    /**
     * @param UUID String to get Invitation
     * @return Invitation
     */
    @Deprecated
    public ControllerContext withInvitation(String UUID){
        this.invitation = invitationService.getInvitationEntityFullByUUID(UUID);
        return this;
    }


    public User getUser() {return user;}
    public Team getTeam() {return team;}
    public Task getTask() {return task;}
    public UserRole getUserRole() {return userRole;}
    public UserRole getOtherRole() {return otherRole;}
    @Deprecated
    public Invitation getInvitation(){return invitation;}

}
