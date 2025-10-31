package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamAdminDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ImpossibleStateException;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithInvitations;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithTasks;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRoles;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRolesAndTasks;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.Teams.Updaters.TeamUpdatersService;
import com.patrykmarchewka.concordiapi.UserRole;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamUserRoleService teamUserRoleService;
    private final TaskService taskService;
    private final TeamUpdatersService teamUpdatersService;

    @Autowired
    public TeamService(TeamRepository teamRepository, TeamUserRoleService teamUserRoleService, TaskService taskService, TeamUpdatersService teamUpdatersService){
        this.teamRepository = teamRepository;
        this.teamUserRoleService = teamUserRoleService;
        this.taskService = taskService;
        this.teamUpdatersService = teamUpdatersService;
    }

    private static final Map<UserRole, Function<TeamWithUserRolesAndTasks, TeamDTO>> roleToTeamDTO = Map.of(
            UserRole.OWNER, TeamAdminDTO::new,
            UserRole.ADMIN, TeamAdminDTO::new,
            UserRole.MANAGER, TeamManagerDTO::new,
            UserRole.MEMBER, TeamMemberDTO::new
    );


    /**
     * Creates team with specified body details and sets given User as team owner
     * @param body TeamRequestBody with information
     * @param user User creating team that becomes team owner
     * @return Team that has been created
     */
    @Transactional
    public Team createTeam(TeamRequestBody body, User user){
        Team team = new Team();
        teamUpdatersService.createUpdate(team, body);
        team.addUserRole(user,UserRole.OWNER);
        return saveTeam(team);
    }

    /**
     * Edits entire team with values specified in the body
     * @param team Team to edit
     * @param body TeamRequestBody with new values
     * @return Team after changes
     */
    @Transactional
    public Team putTeam(Team team, TeamRequestBody body){
        teamUpdatersService.putUpdate(team, body);
        return saveTeam(team);
    }

    /**
     * Edits team values with those specified in the body
     * @param team Team to edit
     * @param body TeamRequestBody with new values
     * @return Team after changes
     */
    @Transactional
    public Team patchTeam(Team team,TeamRequestBody body){
        teamUpdatersService.patchUpdate(team, body);
        return saveTeam(team);
    }

    /**
     * Saves pending changes to the Team
     * @param team Team to save
     * @return Team after changes
     */
    public Team saveTeam(Team team){return teamRepository.save(team);}

    /**
     * Deletes the team completely
     * @param team Team to delete
     */
    public void deleteTeam(Team team){
        teamRepository.delete(team);
    }

    @Transactional
    public Team addUser(Team team, User user, UserRole role){
        team.addUserRole(user, role);
        return saveTeam(team);
    }

    /**
     * Removes specified user from the team, removes the user from tasks attached to the team and removes role mention for that user. <br>
     * If the team would end up empty without any pending invitations the team gets deleted too
     * @param teamID ID of Team that contains the user that we want to remove
     * @param user User to be removed
     */
    @Transactional
    public Team removeUser(long teamID, User user){
        Team newteam = getTeamEntityFull(teamID);
        removeTeamUserRoleForUser(newteam, user);
        removeTeamTasksForUser(newteam, user);
        return deleteTeamIfEmpty(newteam);
    }

    public void removeTeamUserRoleForUser(Team team, User user){
        if (!Hibernate.isInitialized(team.getUserRoles())){
            throw new ImpossibleStateException("Cannot access userRoles for given team");
        }
        team.removeUserRole(teamUserRoleService.getByUserAndTeam(user, team));
        saveTeam(team);
    }

    public void removeTeamTasksForUser(Team team, User user){
        if (!Hibernate.isInitialized(team.getTeamTasks())){
            throw new ImpossibleStateException("Cannot access teamTasks for given team");
        }
        for (Task task : team.getTeamTasks()){
            if (task.getUsers().contains(user)){
                taskService.removeUserFromTask(task, user);
            }
        }
        saveTeam(team);
    }

    public Team deleteTeamIfEmpty(Team team){
        if (!Hibernate.isInitialized(team.getTeammates()) || !Hibernate.isInitialized(team.getInvitations())){
            throw new ImpossibleStateException("Cannot access userRoles and/or invitations for given team");
        }
        if (team.getTeammates().isEmpty()){
            deleteTeam(team);
            return null;
        }
        return team;
    }

    /**
     * Removes all users from team using {@link #removeUser(long, User)}
     * @param team Team to remove everyone from
     */
    @Transactional
    public void removeAllUsers(Team team){
        for (User user : team.getTeammates()){
            removeUser(team.getID(),user);
        }
    }


    /**
     * @deprecated Deprecated method, pending removal once replacement is made
     * Returns Set of TeamDTO by calling {@link #getTeamDTOByRole(long, long)} (currently calls {@link #getTeamDTOByRole(User, Team)} as former one doesn't work yet)
     * @param user User calling the action
     * @return Set of TeamDTO based on User Role
     */
    @Deprecated
    public Set<TeamDTO> getTeamsDTO(User user){
        return user.getTeams().stream().map(team -> getTeamDTOByRole(user, team)).collect(Collectors.toUnmodifiableSet());
    }


    /**
     * @deprecated Deprecated method, currently used for compatibility. Delete once the replacement {@link #getTeamDTOByRole(long, long)} will work
     * Returns one DTO of a team
     * @param user User calling the action
     * @param team Team to return DTO of
     * @return TeamDTO based on User Role
     * @throws NoPrivilegesException Thrown if user doesn't have enough privileges to generate DTO
     */
    @Deprecated
    public TeamDTO getTeamDTOByRole(User user, Team team){
        UserRole role = teamUserRoleService.getRole(user, team);
        TeamWithUserRolesAndTasks teamWithUserRolesAndTasks = getTeamWithUserRolesAndTasksByID(team.getID());
        return roleToTeamDTO.get(role).apply(teamWithUserRolesAndTasks);
    }

    /**
     * Returns DTO of a team <br>
     * @param userID ID of User calling the action
     * @param teamID ID of Team to return DTO of
     * @return TeamDTO based on User Role
     * @throws NoPrivilegesException Thrown if user doesn't have enough privileges to generate DTO
     */
    public TeamDTO getTeamDTOByRole(final long userID, final long teamID){
        TeamWithUserRolesAndTasks teamWithUserRolesAndTasks = getTeamWithUserRolesAndTasksByID(teamID);
        UserRole role = teamUserRoleService.getRole(userID, teamWithUserRolesAndTasks.getID());
        return roleToTeamDTO.get(role).apply(teamWithUserRolesAndTasks);
    }

    /**
     * Gives the Team based on provided ID
     * @param id ID of the team to get of
     * @return Team that has the specified ID
     */
    public Team getTeamEntityByID(long id){
        return teamRepository.findTeamById(id).orElseThrow(NotFoundException::new);
    }

    public Team getTeamWithUserRoles(Team team){
        return teamRepository.findTeamWithUserRolesAndUsersByID(team.getID()).orElseThrow(() -> new ImpossibleStateException("Team not found with provided ID"));
    }

    public TeamWithUserRoles getTeamWithUserRoles(long teamID){
        return teamRepository.findTeamWithUserRolesByID(teamID).orElseThrow(() -> new ImpossibleStateException("Team not found with provided ID"));
    }

    public TeamWithTasks getTeamWithTeamTasks(long teamID){
        return teamRepository.findTeamWithTeamTasksByID(teamID).orElseThrow(() -> new ImpossibleStateException("Team not found with provided ID"));
    }

    public TeamWithUserRolesAndTasks getTeamWithUserRolesAndTasksByID(long teamID){
        return teamRepository.findTeamWithUserRolesAndTasksByID(teamID).orElseThrow(() -> new ImpossibleStateException("Team not found with provided ID"));
    }

    public TeamWithInvitations getTeamWithInvitations(long teamID){
        return teamRepository.findTeamWithInvitationsByID(teamID).orElseThrow(() -> new ImpossibleStateException("Team not found with provided ID"));
    }

    public Team getTeamEntityFull(long teamID){
        return teamRepository.findTeamEntityFullByID(teamID).orElseThrow(() -> new ImpossibleStateException("Team not found with provided ID"));
    }

    public TeamFull getTeamFull(long teamID){
        return teamRepository.findTeamFullByID(teamID).orElseThrow(() -> new ImpossibleStateException("Team not found with provided ID"));
    }


    /**
     * Deletes everything and flushes
     */
    public void deleteAll() {
        teamRepository.deleteAll();
        teamRepository.flush();
    }
}
