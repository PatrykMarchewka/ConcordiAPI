package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRoleRepository;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class TeamUserRoleService {

    private final TeamUserRoleRepository teamUserRoleRepository;

    @Autowired
    public TeamUserRoleService(TeamUserRoleRepository teamUserRoleRepository){
        this.teamUserRoleRepository = teamUserRoleRepository;
    }

    /**
     * Gets the TeamUserRole object with the given parameters <br>
     * If you want to get {@link UserRole} and not just whole object use {@link #getRole(long, long)} instead
     * @param userID ID of User for whom the object is being retrieved
     * @param teamID ID of Team in which user belongs and has valid UserRole
     * @return TeamUserRole which holds given User, Team and UserRole information
     */
    public TeamUserRole getByUserAndTeam(long userID, long teamID){
        return teamUserRoleRepository.findByUserAndTeam(userID, teamID).orElseThrow(NotFoundException::new);
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
     * @param userID User to change role for
     * @param teamID Team in which the role change occurs
     * @param newRole UserRole to change it to
     */
    public void setRole(long userID, long teamID, UserRole newRole){
        TeamUserRole tmr = getByUserAndTeam(userID, teamID);
        if (tmr.getUserRole() == UserRole.OWNER && !canOwnerLeave(teamID)){
            throw new NoPrivilegesException("Cant demote yourself as the only owner");
        }
        tmr.setUserRole(newRole);
        saveTMR(tmr);
    }

    /**
     * Read-Only, Searches entire team for TeamUserRole with specified role
     * @param teamID ID of Team to search in
     * @param role Role to search for
     * @return Set of TeamUserRole that hold given role and team
     */
    @Transactional(readOnly = true)
    public Set<TeamUserRole> getAllByTeamAndUserRole(final long teamID, final UserRole role){
        Set<TeamUserRole> results = teamUserRoleRepository.findAllByTeamAndUserRole(teamID, role);
        if (results.isEmpty()){
            throw new NotFoundException(String.format("Couldn't find any values for team with ID of %d and user role of %s", teamID, role));
        }
        return results;
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
     * Indicates whether team owner can leave or demote himself by checking if there are other owners to take their place
     * @param teamID ID of Team to check in
     * @return True if owner can leave team/demote himself, otherwise false
     */
    public boolean canOwnerLeave(long teamID){
        return getAllByTeamAndUserRole(teamID, UserRole.OWNER).size() != 1;
    }

    /**
     * Deletes everything and flushes
     */
    public void deleteAll(){
        teamUserRoleRepository.deleteAll();
        teamUserRoleRepository.flush();
    }

}
