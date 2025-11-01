package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRoleRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeamUserRoleService {

    private final TeamUserRoleRepository teamUserRoleRepository;

    @Autowired
    public TeamUserRoleService(TeamUserRoleRepository teamUserRoleRepository){
        this.teamUserRoleRepository = teamUserRoleRepository;
    }

    /**
     * @deprecated Uses old {@link TeamUserRoleRepository#findByUserAndTeam(User, Team)}, new one isn't ready yet so thats why this one is kept and used
     * Gets the TeamUserRole object with the given parameters <br>
     * If you want to get {@link UserRole} and not just whole object use {@link #getRole(User, Team)} instead
     * @param user User for whom the object is being retrieved
     * @param team Team in which user belongs and has valid UserRole
     * @return TeamUserRole which holds given User, Team and UserRole information
     */
    @Deprecated
    public TeamUserRole getByUserAndTeam(User user, Team team){
        return teamUserRoleRepository.findByUserAndTeam(user,team).orElseThrow(NotFoundException::new);
    }

    /**
     * Gets the TeamUserRole object with the given parameters <br>
     * If you want to get {@link UserRole} and not just whole object use {@link #getRole(User, Team)} instead
     * @param userID ID of User for whom the object is being retrieved
     * @param teamID ID of Team in which user belongs and has valid UserRole
     * @return TeamUserRole which holds given User, Team and UserRole information
     */
    public TeamUserRole getByUserAndTeam(long userID, long teamID){
        return teamUserRoleRepository.findByUserAndTeam(userID, teamID).orElseThrow(NotFoundException::new);
    }

    /**
     * @deprecated calls old {@link #getByUserAndTeam(User, Team)}, new one isn't ready yet so thats why this one is kept and used
     * Returns UserRole of User in given Team
     * @param user User of which to get role of
     * @param team Team to check role of user for
     * @return UserRole of User in given Team
     */
    @Deprecated
    public UserRole getRole(User user, Team team){
        return getByUserAndTeam(user,team).getUserRole();
    }

    /**
     * Returns UserRole of User in given Team
     * @param userID ID of User of which to get role of
     * @param teamID ID of Team to check role of user for
     * @return UserRole of User in given Team
     */
    public UserRole getRole(long userID, long teamID){
        return getByUserAndTeam(userID, teamID).getUserRole();
    }

    /**
     * Changes already existing UserRole of User in a team
     * @param myRole Role of the user asking for change
     * @param userID User to change role for
     * @param teamID Team in which the role change occurs
     * @param role UserRole to change it to
     */
    public void setRole(UserRole myRole, long userID, long teamID, UserRole role){
        forceCheckRoles(myRole, role);
        TeamUserRole tmr = getByUserAndTeam(userID, teamID);
        tmr.setUserRole(role);
        saveTMR(tmr);
    }


    /**
     * Read-Only, Searches entire team for users with specified role
     * @param team Team to search in
     * @param role Role to search for
     * @return Set of Users that hold given role in specified team
     */
    @Transactional(readOnly = true)
    public Set<User> getAllByTeamAndUserRole(Team team, UserRole role){
        return teamUserRoleRepository.getAllByTeamAndUserRole(team,role).stream().map(TeamUserRole::getUser).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Saves the TeamUserRole object
     * @param tmr TeamUserRole to save
     * @return TeamUserRole post-save
     */
    public TeamUserRole saveTMR(TeamUserRole tmr){
        return teamUserRoleRepository.save(tmr);
    }


    /**
     * Compares two UserRoles and returns True if first role is same or more privileged than second one, otherwise false
     */
    public boolean checkRoles(UserRole mine, UserRole other){
        return mine.compareTo(other) <= 0;
    }

    /**
     * Compares two UserRoles and potentially throws. If you want to just compare without throwing use {@link #checkRoles(UserRole, UserRole)}
     * @param mine First UserRole
     * @param other Second UserRole
     * @throws NoPrivilegesException Thrown when first role is less privileged than second
     */
    public void forceCheckRoles(UserRole mine, UserRole other){
        if (!checkRoles(mine, other)) throw new NoPrivilegesException();
    }

    /**
     * Deletes everything and flushes
     */
    public void deleteAll(){
        teamUserRoleRepository.deleteAll();
        teamUserRoleRepository.flush();
    }

}
