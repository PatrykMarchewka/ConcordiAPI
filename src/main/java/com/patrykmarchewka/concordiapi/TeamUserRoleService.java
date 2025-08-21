package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRoleRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
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

    @Transactional
    public TeamUserRole createTMR(User user, Team team, UserRole role){
        TeamUserRole tmr = new TeamUserRole();
        tmr.setTeam(team);
        tmr.setUser(user);
        tmr.setUserRole(role);
        return saveTMR(tmr);
    }

    /**
     * Gets the TeamUserRole object with the given parameters <br>
     * If you want to get UserRole and not just whole object use {@link #getRole(User, Team)} instead
     * @param user User for whom the object is being retrieved
     * @param team Team in which user belongs and has valid UserRole
     * @return TeamUserRole which holds given User, Team and UserRole information
     */
    public TeamUserRole getByUserAndTeam(User user, Team team){
        return teamUserRoleRepository.findByUserAndTeam(user,team).orElseThrow(NotFoundException::new);
    }

    /**
     * Deletes the entire TeamUserRole object
     * @param tmr TeamUserRole to delete
     */
    public void deleteTMR(TeamUserRole tmr){
        teamUserRoleRepository.delete(tmr);
    }

    /**
     * Returns UserRole of User in given Team
     * @param user User of which to get role of
     * @param team Team to check role of user for
     * @return UserRole of User in given Team
     */
    public UserRole getRole(User user, Team team){
        TeamUserRole tmr = getByUserAndTeam(user,team);
        return tmr.getUserRole();
    }

    /**
     * Changes UserRole of User in a team
     * @param user User to change role for
     * @param team Team in which the role change occurs
     * @param role UserRole to change it to
     */
    public void setRole(User user, Team team, UserRole role){
        TeamUserRole tmr = getByUserAndTeam(user,team);
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
        return teamUserRoleRepository.findAllByTeamAndUserRole(team,role).stream().map(TeamUserRole::getUser).collect(Collectors.toUnmodifiableSet());
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

}
