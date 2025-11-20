package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamAdminDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithInvitations;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithTasks;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRoles;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRolesAndTasks;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithTeamRoles;
import com.patrykmarchewka.concordiapi.Tasks.TaskService;
import com.patrykmarchewka.concordiapi.Teams.Updaters.TeamUpdatersService;
import com.patrykmarchewka.concordiapi.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
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
    public Team createTeam(@NonNull final TeamRequestBody body, @NonNull final UserWithTeamRoles user){
        Team team = new Team();
        teamUpdatersService.createUpdate(team, body);
        team.addUserRole((User) user,UserRole.OWNER);
        return saveTeam(team);
    }

    /**
     * Edits entire team with values specified in the body
     * @param teamID ID of Team to edit
     * @param body TeamRequestBody with new values
     * @return Team after changes
     */
    @Transactional
    public TeamFull putTeam(final long teamID, @NonNull final TeamRequestBody body){
        Team team = (Team) getTeamFull(teamID);
        teamUpdatersService.putUpdate(team, body);
        return saveTeam(team);
    }

    /**
     * Edits team values with those specified in the body
     * @param teamID ID of Team to edit
     * @param body TeamRequestBody with new values
     * @return Team after changes
     */
    @Transactional
    public TeamFull patchTeam(final long teamID, @NonNull final TeamRequestBody body){
        Team team = (Team) getTeamFull(teamID);
        teamUpdatersService.patchUpdate(team, body);
        return saveTeam(team);
    }

    /**
     * Saves pending changes to the Team
     * @param team Team to save
     * @return Team after changes
     */
    public Team saveTeam(@NonNull final Team team){return teamRepository.save(team);}

    /**
     * Deletes the team completely
     * @param team Team to delete
     */
    public void deleteTeam(@NonNull final Team team){
        teamRepository.delete(team);
    }

    @Transactional
    public TeamWithUserRoles addUser(final long teamID, @NonNull final UserWithTeamRoles user, @NonNull final UserRole role){
        Team team = (Team) getTeamWithUserRoles(teamID);
        if (team.checkUser(user.getID())){
            throw new ConflictException("User is already part of that team");
        }
        team.addUserRole((User) user, role);
        return saveTeam(team);
    }

    /**
     * Removes specified user from the team, removes the user from tasks attached to the team and removes role mention for that user. <br>
     * If the team would end up empty the team gets deleted too
     * @param teamID ID of Team that contains the user that we want to remove
     * @param userID ID of User to be removed
     */
    @Transactional
    public TeamWithUserRolesAndTasks removeUser(final long teamID, final long userID){
        Team team = (Team) getTeamWithUserRolesAndTasksByID(teamID);
        team.removeUserRole(teamUserRoleService.getByUserAndTeam(userID, teamID));
        if (team.getUserRoles().isEmpty()){
            deleteTeam(team);
            return null;
        }
        team.getTeamTasks().stream()
                .filter(task -> task.getUsers().stream().anyMatch(user -> user.getID() == userID))
                .forEach(task -> taskService.removeUserFromTask(task.getID(), teamID, userID));

        return saveTeam(team);
    }


    /**
     * Returns Set of TeamDTO by calling {@link #getTeamDTOByRole(long, long)}, doesnt return null values
     * @param user User calling the action
     * @return Set of TeamDTO based on User Role
     */
    @Transactional(readOnly = true)
    public Set<TeamDTO> getTeamsDTO(@NonNull final UserWithTeamRoles user){
        return user.getTeams().stream().map(team -> getTeamDTOByRole(user.getID(), team.getID())).filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Returns DTO of a team or null value <br>
     * @param userID ID of User calling the action
     * @param teamID ID of Team to return DTO of
     * @return TeamDTO based on User Role or null if user doesnt have enough privileges
     */
    @Transactional(readOnly = true)
    public TeamDTO getTeamDTOByRole(final long userID, final long teamID){
        TeamWithUserRolesAndTasks teamWithUserRolesAndTasks = getTeamWithUserRolesAndTasksByID(teamID);
        UserRole role = teamUserRoleService.getRole(userID, teamWithUserRolesAndTasks.getID());
        var result = roleToTeamDTO.get(role);
        return (result != null) ? result.apply(teamWithUserRolesAndTasks) : null;
    }

    /**
     * Gives the Team based on provided ID
     * @param id ID of the team to get of
     * @return TeamIdentity that has the specified ID
     */
    public TeamIdentity getTeamByID(final long id){
        return teamRepository.findTeamByID(id).orElseThrow(NotFoundException::new);
    }

    public TeamWithUserRoles getTeamWithUserRoles(final long teamID){
        return teamRepository.findTeamWithUserRolesByID(teamID).orElseThrow(NotFoundException::new);
    }

    public TeamWithTasks getTeamWithTasks(final long teamID){
        return teamRepository.findTeamWithTasksByID(teamID).orElseThrow(NotFoundException::new);
    }

    public TeamWithUserRolesAndTasks getTeamWithUserRolesAndTasksByID(final long teamID){
        return teamRepository.findTeamWithUserRolesAndTasksByID(teamID).orElseThrow(NotFoundException::new);
    }

    public TeamWithInvitations getTeamWithInvitations(final long teamID){
        return teamRepository.findTeamWithInvitationsByID(teamID).orElseThrow(NotFoundException::new);
    }

    public TeamFull getTeamFull(final long teamID){
        return teamRepository.findTeamFullByID(teamID).orElseThrow(NotFoundException::new);
    }


    /**
     * Deletes everything and flushes
     */
    public void deleteAll() {
        teamRepository.deleteAll();
        teamRepository.flush();
    }
}
